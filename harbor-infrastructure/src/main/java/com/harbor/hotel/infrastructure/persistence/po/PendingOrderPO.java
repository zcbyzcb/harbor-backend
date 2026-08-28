package com.harbor.hotel.infrastructure.persistence.po;

public record PendingOrderPO(
        String orderId,
        String orderNo,
        String bookerName,
        int roomCount,
        String roomTypeName,
        String plannedCheckinTime) {}
