package com.harbor.hotel.app.order.dto;

import java.time.LocalDate;

public record OrderSearchDTO(
        String orderNo,
        String phone,
        String name,
        String status,
        LocalDate arrivalFrom,
        LocalDate arrivalTo,
        String requestId,
        Long employeeId,
        int pageNo,
        int pageSize) {}
