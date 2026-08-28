package com.harbor.hotel.domain.booking.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository {
    RoomType lockType(Long id);

    Order findRequest(Long employeeId, String key);

    Long orderType(Long orderId);

    Order lockOrder(Long id);

    Long insertOrder(NewOrder order);

    List<Lock> lockReservations(Long orderId);

    void insertReservation(Long orderId, Long inventoryId, int count);

    void updateInventory(Long inventoryId, int booked, int checkedIn, int available);

    void transitionReservation(Long id, String status);

    List<Room> lockRooms(List<Long> ids);

    List<Detail> lockDetails(List<Long> inventories, List<Long> rooms);

    List<Checkin> checkins(Long orderId);

    Long insertCheckin(NewCheckin checkin);

    void insertGuest(Long checkinId, int seq, Guest guest);

    void occupyDetail(Long id, Long checkinId);

    void occupyRoom(Long id);

    void markCheckedIn(Long orderId);

    void markCancelled(Long orderId, Long employeeId, LocalDateTime now, String reason);

    void audit(
            Long orderId,
            String operation,
            String fromStatus,
            String toStatus,
            Long employeeId,
            String requestId);

    int auditCount(Long orderId, String operation);

    record RoomType(Long id, String name, BigDecimal price, int maxGuests) {}

    record Order(
            Long id,
            String orderNo,
            Long employeeId,
            String requestId,
            byte[] requestHash,
            Long roomTypeId,
            int roomCount,
            LocalDateTime checkinTime,
            LocalDateTime checkoutTime,
            int nights,
            String status) {}

    record NewOrder(
            String orderNo,
            Long employeeId,
            String requestId,
            byte[] requestHash,
            Long roomTypeId,
            String roomTypeName,
            int roomCount,
            String bookerName,
            String bookerPhone,
            LocalDateTime checkinTime,
            LocalDateTime checkoutTime,
            int nights,
            BigDecimal nightlyPrice,
            BigDecimal totalAmount,
            String remark) {}

    record Lock(Long id, Long inventoryId, int roomCount, String status) {}

    record Room(Long id, Long roomTypeId, String roomNo, String physicalStatus) {}

    record Detail(
            Long id,
            Long inventoryId,
            Long roomTypeId,
            Long roomId,
            String status,
            int occupied,
            Long checkinId) {}

    record Checkin(Long id, Long roomId, Long employeeId, String requestId, byte[] requestHash) {}

    record NewCheckin(
            Long orderId,
            Long roomTypeId,
            Long roomId,
            String roomNo,
            LocalDateTime now,
            Long employeeId,
            String requestId,
            byte[] requestHash) {}

    record Guest(String name, String phone) {}

    record Allocation(Long roomId, List<Guest> guests) {}
}
