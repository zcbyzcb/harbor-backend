package com.harbor.hotel.domain.booking.model;

import java.time.LocalDateTime;

public record Order(
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
        BookingOrderStatus status) {}
