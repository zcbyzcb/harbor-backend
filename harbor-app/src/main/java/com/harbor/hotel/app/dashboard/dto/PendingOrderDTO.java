package com.harbor.hotel.app.dashboard.dto;

public record PendingOrderDTO(
        String orderId,
        String orderNo,
        String bookerName,
        int roomCount,
        String roomTypeName,
        String plannedCheckinTime) {}
