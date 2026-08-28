package com.harbor.hotel.api.web.dashboard.vo;

public record PendingOrderVO(
        String orderId,
        String orderNo,
        String bookerName,
        int roomCount,
        String roomTypeName,
        String plannedCheckinTime) {}
