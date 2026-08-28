package com.harbor.hotel.domain.booking.factory;

import com.harbor.hotel.domain.booking.model.BookingOrder;
import com.harbor.hotel.domain.booking.model.Reservation;
import com.harbor.hotel.domain.booking.repository.BookingRepository;
import com.harbor.hotel.domain.inventory.repository.InventoryRepository;

import java.time.Clock;

public final class BookingFactory {
    private final BookingRepository bookings;
    private final InventoryRepository inventories;
    private final Clock clock;
    private final int windowDays;

    public BookingFactory(
            BookingRepository bookings,
            InventoryRepository inventories,
            Clock clock,
            int windowDays) {
        this.bookings = bookings;
        this.inventories = inventories;
        this.clock = clock;
        this.windowDays = windowDays;
    }

    public Reservation createReservation() {
        return new Reservation(bookings, inventories, clock, windowDays);
    }

    public BookingOrder order(Long id) {
        return new BookingOrder(bookings, inventories, clock, id);
    }
}
