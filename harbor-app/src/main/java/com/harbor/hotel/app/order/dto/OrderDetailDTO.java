package com.harbor.hotel.app.order.dto;

public record OrderDetailDTO(OrderSummaryDTO order, java.util.List<RegisteredRoomDTO> rooms) {}
