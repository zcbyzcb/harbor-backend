package com.harbor.hotel.domain.booking.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingInput(
        Long roomTypeId,
        LocalDate checkinDate,
        LocalDate checkoutDate,
        int roomCount,
        String bookerName,
        String bookerPhone,
        BigDecimal confirmedPrice,
        String remark) {}
