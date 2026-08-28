package com.harbor.hotel.api.web.order.vo;

public record OrderSummaryVO(
        String id,
        String orderNo,
        String bookerName,
        String bookerPhone,
        String status,
        int roomCount,
        String roomTypeName,
        String plannedCheckinTime,
        String plannedCheckoutTime,
        int nights,
        String nightlyPrice,
        String totalAmount,
        String remark,
        String cancelTime,
        String cancelReason,
        int maxGuests) {}
