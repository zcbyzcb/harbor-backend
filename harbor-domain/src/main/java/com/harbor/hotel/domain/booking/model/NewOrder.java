package com.harbor.hotel.domain.booking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NewOrder(
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
