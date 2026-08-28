package com.harbor.hotel.api.web.order.vo;

public record OrderDetailVO(OrderSummaryVO order, java.util.List<RegisteredRoomVO> rooms) {}
