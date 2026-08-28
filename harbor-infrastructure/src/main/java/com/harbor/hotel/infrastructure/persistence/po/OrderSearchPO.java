package com.harbor.hotel.infrastructure.persistence.po;

import java.time.LocalDate;

public record OrderSearchPO(
        String orderNo,
        String phone,
        String name,
        String status,
        LocalDate arrivalFrom,
        LocalDate arrivalTo,
        String requestId,
        Long employeeId,
        long offset,
        int limit) {}
