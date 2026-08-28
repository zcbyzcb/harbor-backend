package com.harbor.hotel.api.web.order.vo;

public record OrderPageVO(
        java.util.List<OrderSummaryVO> items, long total, int pageNo, int pageSize) {}
