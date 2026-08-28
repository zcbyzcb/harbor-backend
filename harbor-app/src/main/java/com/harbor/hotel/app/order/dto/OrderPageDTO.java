package com.harbor.hotel.app.order.dto;

public record OrderPageDTO(
        java.util.List<OrderSummaryDTO> items, long total, int pageNo, int pageSize) {}
