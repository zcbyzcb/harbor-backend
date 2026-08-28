package com.harbor.hotel.infrastructure.persistence.po;

import java.time.LocalDateTime;

public record OrderPO(
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
