package com.harbor.hotel.app.booking.transfer;

import com.harbor.hotel.app.booking.dto.*;
import com.harbor.hotel.domain.booking.model.Allocation;
import com.harbor.hotel.domain.booking.model.BookingInput;
import com.harbor.hotel.domain.booking.model.Guest;

import java.util.List;

public final class BookingCommandTransfer {
    private BookingCommandTransfer() {}

    public static BookingInput toDomain(BookingCommandDTO c) {
        return new BookingInput(
                c.roomTypeId(),
                c.checkinDate(),
                c.checkoutDate(),
                c.roomCount(),
                clean(c.bookerName()),
                clean(c.bookerPhone()),
                c.confirmedPrice(),
                clean(c.remark()));
    }

    public static List<Allocation> toDomain(CheckInCommandDTO c) {
        return c.rooms().stream()
                .map(
                        a ->
                                new Allocation(
                                        a.roomId(),
                                        a.guests().stream()
                                                .map(
                                                        g ->
                                                                new Guest(
                                                                        clean(g.name()),
                                                                        clean(g.phone()),
                                                                        clean(g.identityNo())))
                                                .toList()))
                .toList();
    }

    public static String clean(String text) {
        return text == null ? "" : text.trim();
    }
}
