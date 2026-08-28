package com.harbor.hotel.app.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingCommandDTO(
        Long roomTypeId,
        LocalDate checkinDate,
        LocalDate checkoutDate,
        int roomCount,
        String bookerName,
        String bookerPhone,
        BigDecimal confirmedPrice,
        String remark,
        Long employeeId,
        String requestId) {}
