package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.booking.repository.BookingRepository;
import com.harbor.hotel.domain.booking.validator.CheckInValidator;
import com.harbor.hotel.domain.inventory.model.InventoryState;
import com.harbor.hotel.domain.inventory.model.InventorySnapshot;
import com.harbor.hotel.domain.inventory.repository.InventoryRepository;
import com.harbor.hotel.domain.shared.DomainException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/** Whole-order transitions; all persistence participates in the Processor's transaction. */
public final class BookingOrder {
    private final BookingRepository bookings;
    private final InventoryRepository inventories;
    private final Clock clock;
    private final Long id;

    public BookingOrder(BookingRepository b, InventoryRepository i, Clock c, Long id) {
        this.bookings = b;
        this.inventories = i;
        this.clock = c;
        this.id = id;
    }

    private Order lock() {
        Long typeId = bookings.orderType(id);
        if (typeId == null) throw new DomainException("ORDER_NOT_FOUND");
        if (bookings.lockType(typeId) == null) throw new DomainException("ROOM_TYPE_NOT_FOUND");
        Order order = bookings.lockOrder(id);
        if (order == null) throw new DomainException("ORDER_NOT_FOUND");
        return order;
    }

    private List<InventoryState> inventory(Order order) {
        List<InventoryState> result = new ArrayList<>();
        for (LocalDate date = order.checkinTime().toLocalDate();
                date.isBefore(order.checkoutTime().toLocalDate());
                date = date.plusDays(1)) {
            InventorySnapshot row =
                    inventories.findByRoomTypeAndDate(order.roomTypeId(), date);
            if (row == null) throw new DomainException("INVENTORY_NOT_READY");
            if (!inventories.isConsistent(row.id()))
                throw new DomainException("INVENTORY_DATA_INCONSISTENT");
            result.add(
                    new InventoryState(
                            row.id(),
                            order.roomTypeId(),
                            row.totalRooms(),
                            row.bookedRooms(),
                            row.checkedInRooms(),
                            row.availableRooms()));
        }
        if (result.size() != order.nights())
            throw new DomainException("INVENTORY_DATA_INCONSISTENT");
        return result;
    }

    private List<Lock> locks(Order order, List<InventoryState> states, String expected) {
        List<Lock> locks = bookings.lockReservations(id);
        Set<Long> ids = new HashSet<>(states.stream().map(InventoryState::id).toList());
        if (locks.size() != states.size()
                || locks.stream()
                        .anyMatch(
                                l ->
                                        !ids.remove(l.inventoryId())
                                                || l.roomCount() != order.roomCount()
                                                || !expected.equals(l.status()))
                || !ids.isEmpty()) throw new DomainException("INVENTORY_DATA_INCONSISTENT");
        return locks;
    }

    public Long cancel(Long employeeId, String requestKey, String reason) {
        String key = RequestFingerprint.key(requestKey);
        Order order = lock();
        if ("CHECKED_IN".equals(order.status())) throw new DomainException("ORDER_STATUS_CONFLICT");
        List<InventoryState> states = inventory(order);
        if ("CANCELLED".equals(order.status())) {
            locks(order, states, "cancel");
            if (bookings.auditCount(id, "CANCEL") != 1)
                throw new DomainException("INVENTORY_DATA_INCONSISTENT");
            return id;
        }
        if (!"PENDING".equals(order.status())) throw new DomainException("ORDER_STATUS_CONFLICT");
        List<Lock> locks = locks(order, states, "locked");
        for (InventoryState state : states) {
            state.cancelReservation(order.roomCount());
            bookings.updateInventory(
                    state.id(),
                    state.bookedRooms(),
                    state.checkedInRooms(),
                    state.availableRooms(),
                    state.bookedRooms() + order.roomCount(),
                    state.checkedInRooms(),
                    state.availableRooms() - order.roomCount());
        }
        for (Lock record : locks) bookings.transitionReservation(record.id(), "cancel");
        bookings.markCancelled(id, employeeId, LocalDateTime.now(clock), reason);
        bookings.audit(id, "CANCEL", "PENDING", "CANCELLED", employeeId, key);
        return id;
    }

    public Long checkIn(Long employeeId, String requestKey, List<Allocation> allocations) {
        String key = RequestFingerprint.key(requestKey);
        CheckInValidator.validateStructure(allocations);
        List<Allocation> sorted =
                allocations.stream().sorted(Comparator.comparing(Allocation::roomId)).toList();
        List<Object> fields = new ArrayList<>();
        fields.add(id);
        for (Allocation a : sorted) {
            fields.add(a.roomId());
            fields.add(a.guests().size());
            for (Guest guest : a.guests()) {
                fields.add(guest.name());
                fields.add(guest.phone());
            }
        }
        byte[] hash = RequestFingerprint.hash(fields.toArray());
        Order order = lock();
        if ("CANCELLED".equals(order.status())) throw new DomainException("ORDER_STATUS_CONFLICT");
        List<InventoryState> states = inventory(order);
        if ("CHECKED_IN".equals(order.status())) {
            locks(order, states, "release");
            List<Checkin> records = bookings.checkins(id);
            if (records.size() != order.roomCount() || bookings.auditCount(id, "CHECK_IN") != 1)
                throw new DomainException("INVENTORY_DATA_INCONSISTENT");
            for (Checkin record : records) {
                if (!record.employeeId().equals(employeeId) || !record.requestId().equals(key))
                    throw new DomainException("ORDER_STATUS_CONFLICT");
                RequestFingerprint.same(record.requestHash(), hash);
            }
            List<Detail> details =
                    bookings.lockDetails(
                            states.stream().map(InventoryState::id).toList(),
                            records.stream().map(Checkin::roomId).sorted().toList());
            if (details.size() != order.roomCount() * order.nights()
                    || details.stream()
                            .anyMatch(
                                    d ->
                                            d.occupied() != 1
                                                    || records.stream()
                                                            .noneMatch(
                                                                    r ->
                                                                            r.id().equals(
                                                                                                    d
                                                                                                            .checkinId())
                                                                                    && r.roomId()
                                                                                            .equals(
                                                                                                    d
                                                                                                            .roomId()))))
                throw new DomainException("INVENTORY_DATA_INCONSISTENT");
            return id;
        }
        if (!"PENDING".equals(order.status())) throw new DomainException("ORDER_STATUS_CONFLICT");
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.isBefore(order.checkinTime()) || !now.isBefore(order.checkoutTime()))
            throw new DomainException("CHECKIN_TIME_INVALID");
        List<Lock> locks = locks(order, states, "locked");
        int maxGuests = bookings.lockType(order.roomTypeId()).maxGuests();
        CheckInValidator.validateAllocations(sorted, order.roomCount(), maxGuests);
        List<Room> rooms = bookings.lockRooms(sorted.stream().map(Allocation::roomId).toList());
        if (rooms.size() != order.roomCount()
                || rooms.stream()
                        .anyMatch(
                                r ->
                                        !r.roomTypeId().equals(order.roomTypeId())
                                                || !"READY".equals(r.physicalStatus())))
            throw new DomainException("ROOM_NOT_AVAILABLE");
        List<Detail> details =
                bookings.lockDetails(
                        states.stream().map(InventoryState::id).toList(),
                        sorted.stream().map(Allocation::roomId).toList());
        if (details.size() != order.roomCount() * order.nights()
                || details.stream()
                        .anyMatch(
                                d ->
                                        !d.roomTypeId().equals(order.roomTypeId())
                                                || !"ACTIVE".equals(d.status())
                                                || d.occupied() != 0
                                                || d.checkinId() != null))
            throw new DomainException("ROOM_NOT_AVAILABLE");
        for (InventoryState state : states) {
            state.convertToCheckin(order.roomCount());
            bookings.updateInventory(
                    state.id(),
                    state.bookedRooms(),
                    state.checkedInRooms(),
                    state.availableRooms(),
                    state.bookedRooms() + order.roomCount(),
                    state.checkedInRooms() - order.roomCount(),
                    state.availableRooms());
        }
        for (Lock record : locks) bookings.transitionReservation(record.id(), "release");
        for (Allocation allocation : sorted) {
            Room room =
                    rooms.stream()
                            .filter(r -> r.id().equals(allocation.roomId()))
                            .findFirst()
                            .orElseThrow();
            Long checkinId =
                    bookings.insertCheckin(
                            new NewCheckin(
                                    id,
                                    order.roomTypeId(),
                                    room.id(),
                                    room.roomNo(),
                                    now,
                                    employeeId,
                                    key,
                                    hash));
            for (int index = 0; index < allocation.guests().size(); index++)
                bookings.insertGuest(checkinId, index + 1, allocation.guests().get(index));
            for (Detail detail : details)
                if (detail.roomId().equals(room.id()))
                    bookings.occupyDetail(detail.id(), checkinId);
            bookings.occupyRoom(room.id());
        }
        bookings.markCheckedIn(id);
        bookings.audit(id, "CHECK_IN", "PENDING", "CHECKED_IN", employeeId, key);
        return id;
    }
}
