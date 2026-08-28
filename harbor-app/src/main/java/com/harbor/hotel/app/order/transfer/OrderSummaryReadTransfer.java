package com.harbor.hotel.app.order.transfer;

import com.harbor.hotel.app.order.dto.OrderSummaryDTO;
import com.harbor.hotel.infrastructure.persistence.po.OrderSummaryPO;

public final class OrderSummaryReadTransfer {
    private OrderSummaryReadTransfer() {}

    public static OrderSummaryDTO toDTO(OrderSummaryPO p) {
        return p == null
                ? null
                : new OrderSummaryDTO(
                        p.id(),
                        p.orderNo(),
                        p.bookerName(),
                        p.bookerPhone(),
                        p.status(),
                        p.roomCount(),
                        p.roomTypeName(),
                        p.plannedCheckinTime(),
                        p.plannedCheckoutTime(),
                        p.nights(),
                        p.nightlyPrice(),
                        p.totalAmount(),
                        p.remark(),
                        p.cancelTime(),
                        p.cancelReason(),
                        p.maxGuests());
    }
}
