package com.harbor.hotel.domain.booking.model;

import java.time.LocalDateTime;

public record NewCheckin(
        Long orderId,
        Long roomTypeId,
        Long roomId,
        String roomNo,
        LocalDateTime now,
        Long employeeId,
        String requestId,
        byte[] requestHash) {}
