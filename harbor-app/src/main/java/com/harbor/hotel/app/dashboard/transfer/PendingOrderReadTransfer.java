package com.harbor.hotel.app.dashboard.transfer;

import com.harbor.hotel.app.dashboard.dto.PendingOrderDTO;
import com.harbor.hotel.infrastructure.persistence.po.PendingOrderPO;

public final class PendingOrderReadTransfer {
    private PendingOrderReadTransfer() {}

    public static PendingOrderDTO toDTO(PendingOrderPO p) {
        return p == null
                ? null
                : new PendingOrderDTO(
                        p.orderId(),
                        p.orderNo(),
                        p.bookerName(),
                        p.roomCount(),
                        p.roomTypeName(),
                        p.plannedCheckinTime());
    }
}
