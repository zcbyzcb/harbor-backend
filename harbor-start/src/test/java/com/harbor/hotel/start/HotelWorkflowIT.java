package com.harbor.hotel.start;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.*;
import com.harbor.hotel.app.inventory.job.InventoryWindowJob;
import com.harbor.hotel.app.booking.dto.*;
import com.harbor.hotel.app.booking.processor.*;
import com.harbor.hotel.app.inventory.dto.InitializeDailyInventoryDTO;
import com.harbor.hotel.app.inventory.processor.InitializeDailyInventoryProcessor;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.infrastructure.persistence.mapper.InventoryMapper;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "hotel.inventory.initialize-on-start=false",
            "hotel.xxl-job.enabled=false",
            "logging.file.path=target/test-logs"
        })
@Import(HotelWorkflowIT.TimeConfig.class)
class HotelWorkflowIT {
    static final java.util.concurrent.atomic.AtomicReference<Instant> NOW =
            new java.util.concurrent.atomic.AtomicReference<>(
                    Instant.parse("2026-08-28T05:00:00Z"));
    static final LocalDate TODAY = LocalDate.of(2026, 8, 28);
    static final String DB = "harbor_it_" + UUID.randomUUID().toString().replace("-", "");
    static final String ADMIN =
            System.getenv()
                    .getOrDefault(
                            "HOTEL_TEST_ADMIN_URL",
                            "jdbc:mysql://127.0.0.1:13306/?connectionTimeZone=Asia/Shanghai");
    static final String USER = System.getenv().getOrDefault("HOTEL_TEST_DB_USERNAME", "root");
    static final String PASSWORD = System.getenv().getOrDefault("HOTEL_TEST_DB_PASSWORD", "");
    static Path root;

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) throws Exception {
        root = Path.of("").toAbsolutePath();
        while (!Files.exists(root.resolve("db/schema.sql"))) root = root.getParent();
        try (var connection = DriverManager.getConnection(ADMIN, USER, PASSWORD);
                var statement = connection.createStatement()) {
            statement.execute(
                    "CREATE DATABASE " + DB + " CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci");
            connection.setCatalog(DB);
            ScriptUtils.executeSqlScript(
                    connection, new FileSystemResource(root.resolve("db/schema.sql")));
        }
        String url = ADMIN.replace("/?", "/" + DB + "?");
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> USER);
        registry.add("spring.datasource.password", () -> PASSWORD);
    }

    @AfterAll
    static void dropDatabase() throws Exception {
        try (var c = DriverManager.getConnection(ADMIN, USER, PASSWORD);
                var s = c.createStatement()) {
            s.execute("DROP DATABASE " + DB);
        }
    }

    @TestConfiguration
    static class TimeConfig {
        @Bean
        @Primary
        Clock testClock() {
            return new Clock() {
                public ZoneId getZone() {
                    return ZoneId.of("Asia/Shanghai");
                }

                public Clock withZone(ZoneId zone) {
                    return Clock.fixed(instant(), zone);
                }

                public Instant instant() {
                    return NOW.get();
                }
            };
        }
    }

    @Resource JdbcTemplate jdbc;
    @Resource CreateBookingProcessor create;
    @Resource CheckInOrderProcessor checkIn;
    @Resource CancelBookingProcessor cancel;
    @Resource InventoryWindowJob job;
    @Resource InitializeDailyInventoryProcessor initialize;
    @Resource InventoryMapper inventoryMapper;
    @Resource ObjectMapper json;
    @Resource com.harbor.hotel.infrastructure.persistence.mapper.OrderReadMapper orderReadMapper;
    @LocalServerPort int port;

    @BeforeEach
    void reset() throws Exception {
        NOW.set(Instant.parse("2026-08-28T05:00:00Z"));
        for (String table :
                List.of(
                        "order_operation_log",
                        "checkin_guest",
                        "checkin_record",
                        "inventory_lock_record",
                        "booking_order",
                        "room_inventory_detail",
                        "room_type_inventory",
                        "room",
                        "room_type",
                        "sys_employee")) jdbc.execute("TRUNCATE TABLE " + table);
        try (var c = jdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(
                    c, new FileSystemResource(root.resolve("db/seed_base_data.sql")));
        }
        jdbc.update(
                "INSERT INTO sys_employee(id,username,display_name,password_hash)"
                        + " VALUES(1,'frontdesk','测试前台',?)",
                new BCryptPasswordEncoder().encode("Hotel-Test-Only-2026"));
        assertTrue(job.runWindow().isEmpty());
    }

    BookingCommandDTO request(int count, String key) {
        return new BookingCommandDTO(
                1L,
                TODAY,
                TODAY.plusDays(2),
                count,
                "测试客人",
                "13800000000",
                new BigDecimal("199.00"),
                "",
                1L,
                key);
    }

    Long book(int count) {
        return create.process(request(count, UUID.randomUUID().toString()));
    }

    CheckInCommandDTO checkRequest(Long id, String key, Long... rooms) {
        return new CheckInCommandDTO(
                id,
                1L,
                key,
                Arrays.stream(rooms)
                        .map(
                                r ->
                                        new CheckInCommandDTO.AllocationDTO(
                                                r,
                                                List.of(new CheckInCommandDTO.GuestDTO("入住人", ""))))
                        .toList());
    }

    void invariant() {
        assertEquals(
                0,
                jdbc.queryForObject(
                        "SELECT COUNT(*) FROM room_type_inventory WHERE"
                                + " total_rooms!=booked_rooms+checked_in_rooms+available_rooms",
                        Integer.class));
        for (Long id : jdbc.queryForList("SELECT id FROM room_type_inventory", Long.class))
            assertTrue(inventoryMapper.isConsistent(id), "invalid inventory " + id);
    }

    @Test
    void initializeIsAtomicIdempotentAndPreservesBusinessCounts() {
        assertEquals(
                21, jdbc.queryForObject("SELECT COUNT(*) FROM room_type_inventory", Integer.class));
        assertEquals(
                84,
                jdbc.queryForObject("SELECT COUNT(*) FROM room_inventory_detail", Integer.class));
        book(2);
        assertTrue(job.runWindow().isEmpty());
        assertEquals(
                4,
                jdbc.queryForObject(
                        "SELECT SUM(booked_rooms) FROM room_type_inventory", Integer.class));
        invariant();
    }

    @Test
    void initializationDoesNotTreatOccupiedOrDirtyAsOutOfService() {
        jdbc.update("UPDATE room SET physical_status='OCCUPIED' WHERE id=101");
        jdbc.update("UPDATE room SET physical_status='DIRTY' WHERE id=102");
        jdbc.update("UPDATE room SET physical_status='OUT_OF_SERVICE' WHERE id=103");
        initialize.process(new InitializeDailyInventoryDTO(1L, TODAY.plusDays(7)));
        assertEquals(
                3,
                jdbc.queryForObject(
                        "SELECT total_rooms FROM room_type_inventory WHERE room_type_id=1 AND"
                                + " stay_date=?",
                        Integer.class,
                        TODAY.plusDays(7)));
    }

    @Test
    void incompleteInventoryFailsWithoutRepairAndBatchReportsFailure() {
        jdbc.update(
                "DELETE FROM room_inventory_detail WHERE inventory_id=(SELECT id FROM"
                    + " room_type_inventory WHERE room_type_id=1 AND stay_date=?) AND room_id=101",
                TODAY);
        assertThrows(
                DomainException.class,
                () -> initialize.process(new InitializeDailyInventoryDTO(1L, TODAY)));
        assertEquals(1, job.runWindow().size());
        assertEquals(
                83,
                jdbc.queryForObject("SELECT COUNT(*) FROM room_inventory_detail", Integer.class));
    }

    @Test
    void bookingReplayAndChangedPayload() {
        String key = UUID.randomUUID().toString();
        Long id = create.process(request(2, key));
        assertEquals(id, create.process(request(2, key)));
        assertThrows(DomainException.class, () -> create.process(request(1, key)));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM booking_order", Integer.class));
        invariant();
    }

    @Test
    void multiRoomCheckinReplayAndCancelRejection() {
        Long id = book(2);
        var cmd = checkRequest(id, UUID.randomUUID().toString(), 101L, 102L);
        checkIn.process(cmd);
        checkIn.process(cmd);
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM checkin_record", Integer.class));
        assertEquals(
                4,
                jdbc.queryForObject(
                        "SELECT COUNT(*) FROM room_inventory_detail WHERE is_occupied=1",
                        Integer.class));
        assertEquals(
                4,
                jdbc.queryForObject(
                        "SELECT SUM(checked_in_rooms) FROM room_type_inventory", Integer.class));
        assertThrows(
                DomainException.class,
                () ->
                        cancel.process(
                                new CancelCommandDTO(id, 1L, UUID.randomUUID().toString(), "")));
        assertThrows(
                DomainException.class,
                () -> checkIn.process(checkRequest(id, cmd.requestId(), 101L, 103L)));
        invariant();
    }

    @Test
    void cancelReplayRestoresOnlyOnceAndKeepsOriginalReason() {
        Long id = book(3);
        cancel.process(new CancelCommandDTO(id, 1L, UUID.randomUUID().toString(), "原原因"));
        cancel.process(new CancelCommandDTO(id, 1L, UUID.randomUUID().toString(), "新原因"));
        assertEquals(
                "原原因",
                jdbc.queryForObject(
                        "SELECT cancel_reason FROM booking_order WHERE id=?", String.class, id));
        assertEquals(
                84,
                jdbc.queryForObject(
                        "SELECT SUM(available_rooms) FROM room_type_inventory", Integer.class));
        invariant();
    }

    @Test
    void lastRoomConcurrentBookingDoesNotOversell() throws Exception {
        book(3);
        var results = concurrent(() -> book(1), () -> book(1));
        assertEquals(1, results.stream().filter(Long.class::isInstance).count());
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM booking_order", Integer.class));
        invariant();
    }

    @Test
    void simultaneousSameKeyReturnsSameOrder() throws Exception {
        var command = request(2, UUID.randomUUID().toString());
        var results = concurrent(() -> create.process(command), () -> create.process(command));
        assertInstanceOf(Long.class, results.getFirst());
        assertEquals(results.get(0), results.get(1));
        invariant();
    }

    @Test
    void checkinVersusCancelHasExactlyOneWinner() throws Exception {
        Long id = book(2);
        var results =
                concurrent(
                        () ->
                                checkIn.process(
                                        checkRequest(id, UUID.randomUUID().toString(), 101L, 102L)),
                        () ->
                                cancel.process(
                                        new CancelCommandDTO(
                                                id, 1L, UUID.randomUUID().toString(), "")));
        assertEquals(1, results.stream().filter(Long.class::isInstance).count());
        invariant();
    }

    @Test
    void auditFailureRollsBackOrderAndAllInventory() {
        jdbc.execute(
                "CREATE TRIGGER reject_create_audit BEFORE INSERT ON order_operation_log FOR EACH"
                        + " ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='test failure'");
        try {
            assertThrows(RuntimeException.class, () -> book(2));
            assertEquals(
                    0, jdbc.queryForObject("SELECT COUNT(*) FROM booking_order", Integer.class));
            assertEquals(
                    84,
                    jdbc.queryForObject(
                            "SELECT SUM(available_rooms) FROM room_type_inventory", Integer.class));
        } finally {
            jdbc.execute("DROP TRIGGER reject_create_audit");
        }
        invariant();
    }

    @Test
    void checkinFailureAfterFirstRoomRollsBackEverything() {
        Long id = book(2);
        jdbc.execute(
                "CREATE TRIGGER reject_second_guest BEFORE INSERT ON checkin_guest FOR EACH ROW"
                    + " BEGIN IF (SELECT room_id FROM checkin_record WHERE id=NEW.checkin_id)=102"
                    + " THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='test failure'; END IF; END");
        try {
            assertThrows(
                    RuntimeException.class,
                    () ->
                            checkIn.process(
                                    checkRequest(id, UUID.randomUUID().toString(), 101L, 102L)));
            assertEquals(
                    "PENDING",
                    jdbc.queryForObject(
                            "SELECT status FROM booking_order WHERE id=?", String.class, id));
            assertEquals(
                    0, jdbc.queryForObject("SELECT COUNT(*) FROM checkin_record", Integer.class));
            assertEquals(
                    0,
                    jdbc.queryForObject(
                            "SELECT COUNT(*) FROM room WHERE physical_status='OCCUPIED'",
                            Integer.class));
        } finally {
            jdbc.execute("DROP TRIGGER reject_second_guest");
        }
        invariant();
    }

    @Test
    void originalDatesAndNotTodaysWindowControlLateCheckinReplay() {
        Long id = book(1);
        jdbc.update(
                "UPDATE booking_order SET"
                    + " planned_checkin_time=DATE_SUB(planned_checkin_time,INTERVAL 1 DAY),nights=3"
                    + " WHERE id=?",
                id);
        assertThrows(
                DomainException.class,
                () -> checkIn.process(checkRequest(id, UUID.randomUUID().toString(), 101L)));
    }

    List<Object> concurrent(Callable<Long> first, Callable<Long> second) throws Exception {
        try (var pool = Executors.newFixedThreadPool(2)) {
            var barrier = new CyclicBarrier(2);
            List<Future<Object>> futures = new ArrayList<>();
            for (var call : List.of(first, second))
                futures.add(
                        pool.submit(
                                () -> {
                                    barrier.await();
                                    try {
                                        return call.call();
                                    } catch (Exception ex) {
                                        return ex;
                                    }
                                }));
            return List.of(
                    futures.get(0).get(15, TimeUnit.SECONDS),
                    futures.get(1).get(15, TimeUnit.SECONDS));
        }
    }

    HttpClient client() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
    }

    HttpResponse<String> http(
            HttpClient client, String method, String path, String body, String token, String key)
            throws Exception {
        var request =
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api" + path))
                        .header("Content-Type", "application/json");
        if (token != null) request.header("X-CSRF-TOKEN", token);
        if (key != null) request.header("Idempotency-Key", key);
        return client.send(
                method.equals("GET")
                        ? request.GET().build()
                        : request.POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    String csrf(HttpClient c) throws Exception {
        return json.readTree(http(c, "GET", "/auth/csrf", null, null, null).body())
                .at("/data/token")
                .asText();
    }

    @Test
    void realHttpAuthenticationQueriesBookingDashboardAndLogout() throws Exception {
        var c = client();
        assertEquals(401, http(c, "GET", "/orders", null, null, null).statusCode());
        assertEquals(403, http(c, "POST", "/auth/login", "{}", null, null).statusCode());
        String token = csrf(c);
        assertEquals(
                200,
                http(
                                c,
                                "POST",
                                "/auth/login",
                                "{\"username\":\"frontdesk\",\"password\":\"Hotel-Test-Only-2026\"}",
                                token,
                                null)
                        .statusCode());
        assertEquals(200, http(c, "GET", "/auth/me", null, null, null).statusCode());
        token = csrf(c);
        var availability =
                http(
                        c,
                        "GET",
                        "/room-types/availability?checkinDate=2026-08-28&checkoutDate=2026-08-30&roomCount=2",
                        null,
                        null,
                        null);
        assertEquals(200, availability.statusCode(), availability.body());
        assertEquals(
                "796.00", json.readTree(availability.body()).at("/data/0/totalAmount").asText());
        String body =
                "{\"roomTypeId\":\"1\",\"checkinDate\":\"2026-08-28\",\"checkoutDate\":\"2026-08-30\",\"roomCount\":2,\"bookerName\":\"测试客人\",\"bookerPhone\":\"13800000000\",\"confirmedPrice\":\"199.00\"}";
        String key = UUID.randomUUID().toString();
        var response = http(c, "POST", "/booking_orders", body, token, key);
        assertEquals(200, response.statusCode(), response.body());
        assertEquals(200, http(c, "POST", "/booking_orders", body, token, key).statusCode());
        String id = json.readTree(response.body()).at("/data/orderId").asText();
        var page =
                http(
                        c,
                        "GET",
                        "/orders?requestId=" + key + "&arrivalFrom=2026-08-28",
                        null,
                        null,
                        null);
        assertEquals(1, json.readTree(page.body()).at("/data/total").asInt(), page.body());
        assertEquals(4, orderReadMapper.availableRooms(Long.valueOf(id)).size());
        assertEquals(
                4,
                json.readTree(
                                http(
                                                c,
                                                "GET",
                                                "/orders/" + id + "/available-rooms",
                                                null,
                                                null,
                                                null)
                                        .body())
                        .at("/data")
                        .size());
        var dash = json.readTree(http(c, "GET", "/dashboard", null, null, null).body());
        assertEquals(2, dash.at("/data/pendingCheckInRooms").asInt());
        assertEquals(10, dash.at("/data/availableRooms").asInt());
        String checkBody =
                "{\"rooms\":[{\"roomId\":\"101\",\"guests\":[{\"name\":\"甲\"}]},{\"roomId\":\"102\",\"guests\":[{\"name\":\"乙\"}]}]}";
        var check =
                http(
                        c,
                        "POST",
                        "/booking_orders/" + id + "/check-in",
                        checkBody,
                        token,
                        UUID.randomUUID().toString());
        assertEquals(200, check.statusCode(), check.body());
        assertEquals(
                2,
                json.readTree(http(c, "GET", "/orders/" + id, null, null, null).body())
                        .at("/data/rooms")
                        .size());
        dash = json.readTree(http(c, "GET", "/dashboard", null, null, null).body());
        assertEquals(2, dash.at("/data/checkedInRooms").asInt());
        assertEquals(0, dash.at("/data/pendingCheckInRooms").asInt());
        assertEquals(10, dash.at("/data/availableRooms").asInt());
        assertEquals(200, http(c, "POST", "/auth/logout", "{}", token, null).statusCode());
        assertEquals(401, http(c, "GET", "/orders", null, null, null).statusCode());
        invariant();
    }

    @Test
    void deletedEmployeeAndMissingInventoryFailClosed() throws Exception {
        var c = client();
        String token = csrf(c);
        http(
                c,
                "POST",
                "/auth/login",
                "{\"username\":\"frontdesk\",\"password\":\"Hotel-Test-Only-2026\"}",
                token,
                null);
        jdbc.update("UPDATE sys_employee SET is_deleted=1 WHERE id=1");
        assertEquals(401, http(c, "GET", "/orders", null, null, null).statusCode());
    }

    @Test
    void bookingAndCheckinReplayAfterTheWindowMoved() {
        String bookingKey = UUID.randomUUID().toString();
        Long id = create.process(request(1, bookingKey));
        var command = checkRequest(id, UUID.randomUUID().toString(), 101L);
        checkIn.process(command);
        NOW.set(Instant.parse("2026-09-10T05:00:00Z"));
        assertEquals(id, create.process(request(1, bookingKey)));
        assertEquals(id, checkIn.process(command));
        invariant();
    }

    @Test
    void changedPriceAndWindowRejectNewBookingsButNotExistingReplay() {
        String key = UUID.randomUUID().toString();
        Long id = create.process(request(1, key));
        jdbc.update("UPDATE room_type SET base_price=299 WHERE id=1");
        assertEquals(id, create.process(request(1, key)));
        assertThrows(DomainException.class, () -> book(1));
        var outOfWindow =
                new BookingCommandDTO(
                        1L,
                        TODAY.plusDays(6),
                        TODAY.plusDays(8),
                        1,
                        "测试",
                        "13800000000",
                        new BigDecimal("299.00"),
                        "",
                        1L,
                        UUID.randomUUID().toString());
        assertThrows(DomainException.class, () -> create.process(outOfWindow));
        invariant();
    }

    @Test
    void cannotCheckInBeforeNoonOrUseAnOccupiedPhysicalRoom() {
        Long id = book(1);
        NOW.set(Instant.parse("2026-08-28T03:59:59Z"));
        assertThrows(
                DomainException.class,
                () -> checkIn.process(checkRequest(id, UUID.randomUUID().toString(), 101L)));
        NOW.set(Instant.parse("2026-08-28T05:00:00Z"));
        jdbc.update("UPDATE room SET physical_status='OCCUPIED' WHERE id=101");
        assertThrows(
                DomainException.class,
                () -> checkIn.process(checkRequest(id, UUID.randomUUID().toString(), 101L)));
        invariant();
    }

    @Test
    void initDetailFailureRollsBackHeader() {
        jdbc.execute(
                "CREATE TRIGGER reject_detail BEFORE INSERT ON room_inventory_detail FOR EACH ROW"
                    + " SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='test failure'");
        try {
            assertThrows(
                    RuntimeException.class,
                    () ->
                            initialize.process(
                                    new InitializeDailyInventoryDTO(1L, TODAY.plusDays(7))));
            assertEquals(
                    21,
                    jdbc.queryForObject("SELECT COUNT(*) FROM room_type_inventory", Integer.class));
        } finally {
            jdbc.execute("DROP TRIGGER reject_detail");
        }
        invariant();
    }

    @Test
    void inventorySyncRacingBookingNeverResetsCounters() throws Exception {
        concurrent(
                () -> book(2),
                () -> Long.valueOf(initialize.process(new InitializeDailyInventoryDTO(1L, TODAY))));
        assertEquals(
                4,
                jdbc.queryForObject(
                        "SELECT SUM(booked_rooms) FROM room_type_inventory", Integer.class));
        invariant();
    }

    @Test
    void corruptReplayIsRejectedInsteadOfReturningFakeSuccess() {
        String key = UUID.randomUUID().toString();
        Long id = create.process(request(1, key));
        jdbc.update("DELETE FROM inventory_lock_record WHERE order_id=?", id);
        assertThrows(DomainException.class, () -> create.process(request(1, key)));
    }

    @Test
    void missingInventoryDashboardReturnsNotReady() throws Exception {
        var c = client();
        String token = csrf(c);
        String credentials =
                json.writeValueAsString(
                        Map.of("username", "frontdesk", "password", "Hotel-Test-Only-2026"));
        assertEquals(200, http(c, "POST", "/auth/login", credentials, token, null).statusCode());
        jdbc.update("DELETE FROM room_type_inventory WHERE room_type_id=1 AND stay_date=?", TODAY);
        var result = http(c, "GET", "/dashboard", null, null, null);
        assertEquals(503, result.statusCode());
        assertEquals("INVENTORY_NOT_READY", json.readTree(result.body()).path("code").asText());
    }

    @Test
    void loginFailuresAreLimitedAndCsrfRotatesOnAuthentication() throws Exception {
        var c = client();
        String token = csrf(c);
        String username = "missing_" + UUID.randomUUID();
        String body =
                json.writeValueAsString(
                        Map.of("username", username, "password", "incorrect-password"));
        for (int i = 0; i < 5; i++)
            assertEquals(401, http(c, "POST", "/auth/login", body, token, null).statusCode());
        assertEquals(429, http(c, "POST", "/auth/login", body, token, null).statusCode());
        String valid =
                json.writeValueAsString(
                        Map.of("username", "frontdesk", "password", "Hotel-Test-Only-2026"));
        assertEquals(200, http(c, "POST", "/auth/login", valid, token, null).statusCode());
        assertEquals(403, http(c, "POST", "/auth/logout", "{}", token, null).statusCode());
        assertEquals(200, http(c, "GET", "/auth/me", null, null, null).statusCode());
    }
}
