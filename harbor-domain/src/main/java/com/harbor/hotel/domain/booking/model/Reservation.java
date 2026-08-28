package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.booking.repository.BookingRepository;
import com.harbor.hotel.domain.booking.validator.BookingInputValidator;
import com.harbor.hotel.domain.inventory.model.InventoryState;
import com.harbor.hotel.domain.inventory.model.InventorySnapshot;
import com.harbor.hotel.domain.inventory.repository.InventoryRepository;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;
import com.harbor.hotel.domain.shared.StayPeriod;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;

public final class Reservation {
    private final BookingRepository bookings;
    private final InventoryRepository inventories;
    private final Clock clock;
    private final int window;

    public Reservation(BookingRepository b, InventoryRepository i, Clock c, int w) {
        bookings = b;
        inventories = i;
        clock = c;
        window = w;
    }

    public Long book(BookingInput input, Long employeeId, String requestKey, String orderNo) {
        String key = RequestFingerprint.key(requestKey);
        BookingInputValidator.validate(input, orderNo);
        StayPeriod period = new StayPeriod(input.checkinDate(), input.checkoutDate());
        byte[] hash =
                RequestFingerprint.hash(
                        input.roomTypeId(),
                        input.checkinDate(),
                        input.checkoutDate(),
                        input.roomCount(),
                        input.bookerName(),
                        input.bookerPhone(),
                        input.confirmedPrice().setScale(2).toPlainString(),
                        input.remark());
        Order previous = bookings.findRequest(employeeId, key);
        if (previous != null) {
            bookings.lockType(previous.roomTypeId());
            previous = bookings.lockOrder(previous.id());
            return replay(previous, hash);
        }
        RoomType type = bookings.lockType(input.roomTypeId());
        if (type == null) throw new DomainException(ErrorCode.ROOM_TYPE_NOT_FOUND);
        previous = bookings.findRequest(employeeId, key);
        if (previous != null) {
            return replay(previous, hash);
        }
        LocalDate today = LocalDate.now(clock);
        if (input.checkinDate().isBefore(today)
                || input.checkoutDate().isAfter(today.plusDays(window)))
            throw new DomainException(ErrorCode.BOOKING_WINDOW_INVALID);
        if (type.price().compareTo(input.confirmedPrice()) != 0)
            throw new DomainException(ErrorCode.PRICE_CHANGED);
        ArrayList<InventoryState> states = new ArrayList<>();
        for (LocalDate date = input.checkinDate();
                date.isBefore(input.checkoutDate());
                date = date.plusDays(1)) {
            InventorySnapshot inventory =
                    inventories.findByRoomTypeAndDate(type.id(), date);
            if (inventory == null) throw new DomainException(ErrorCode.INVENTORY_NOT_READY);
            if (!inventories.isConsistent(inventory.id()))
                throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
            InventoryState state =
                    new InventoryState(
                            inventory.id(),
                            type.id(),
                            inventory.totalRooms(),
                            inventory.bookedRooms(),
                            inventory.checkedInRooms(),
                            inventory.availableRooms());
            state.reserve(input.roomCount());
            states.add(state);
        }
        int nights = Math.toIntExact(period.nights());
        java.math.BigDecimal total =
                type.price()
                        .multiply(java.math.BigDecimal.valueOf((long) nights * input.roomCount()));
        if (total.precision() - total.scale() > 10) throw new DomainException(ErrorCode.INVALID_AMOUNT);
        Long id =
                bookings.insertOrder(
                        new NewOrder(
                                orderNo,
                                employeeId,
                                key,
                                hash,
                                type.id(),
                                type.name(),
                                input.roomCount(),
                                input.bookerName(),
                                input.bookerPhone(),
                                input.checkinDate().atTime(12, 0),
                                input.checkoutDate().atTime(12, 0),
                                nights,
                                type.price(),
                                total,
                                input.remark()));
        for (InventoryState state : states) {
            bookings.reserveInventory(state.id(), input.roomCount());
            bookings.insertReservation(id, state.id(), input.roomCount());
        }
        bookings.audit(id, OrderOperation.CREATE, null, BookingOrderStatus.PENDING, employeeId, key);
        return id;
    }

    private Long replay(Order previous, byte[] hash) {
        RequestFingerprint.same(previous.requestHash(), hash);
        java.util.HashSet<Long> ids = new java.util.HashSet<>();
        for (LocalDate date = previous.checkinTime().toLocalDate();
                date.isBefore(previous.checkoutTime().toLocalDate());
                date = date.plusDays(1)) {
            InventorySnapshot row =
                    inventories.findByRoomTypeAndDate(previous.roomTypeId(), date);
            if (row == null || !inventories.isConsistent(row.id()))
                throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
            ids.add(row.id());
        }
        InventoryLockStatus expected =
                switch (previous.status()) {
                    case PENDING -> InventoryLockStatus.LOCKED;
                    case CHECKED_IN -> InventoryLockStatus.RELEASED;
                    case CANCELLED -> InventoryLockStatus.CANCELLED;
                };
        java.util.List<Lock> locks = bookings.lockReservations(previous.id());
        if (ids.size() != previous.nights()
                || locks.size() != ids.size()
                || locks.stream()
                        .anyMatch(
                                l ->
                                        !ids.remove(l.inventoryId())
                                                || !expected.equals(l.status())
                                                || l.roomCount() != previous.roomCount())
                || !ids.isEmpty()
                || bookings.auditCount(previous.id(), OrderOperation.CREATE) != 1)
            throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
        return previous.id();
    }
}
