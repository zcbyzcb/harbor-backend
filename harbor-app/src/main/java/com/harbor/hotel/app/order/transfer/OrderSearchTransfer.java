package com.harbor.hotel.app.order.transfer;

import com.harbor.hotel.app.order.dto.OrderSearchDTO;
import com.harbor.hotel.infrastructure.persistence.po.OrderSearchPO;

public final class OrderSearchTransfer {
    private OrderSearchTransfer() {}

    public static OrderSearchPO toPO(OrderSearchDTO q) {
        return new OrderSearchPO(
                q.orderNo(),
                q.phone(),
                q.name(),
                q.status(),
                q.arrivalFrom(),
                q.arrivalTo(),
                q.requestId(),
                q.employeeId(),
                (long) (q.pageNo() - 1) * q.pageSize(),
                q.pageSize());
    }
}
