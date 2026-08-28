package com.harbor.hotel.api.web.dashboard.transfer;

import com.harbor.hotel.api.web.dashboard.vo.DashboardVO;
import com.harbor.hotel.api.web.dashboard.vo.PendingOrderVO;
import com.harbor.hotel.app.dashboard.dto.DashboardDTO;
import com.harbor.hotel.app.dashboard.dto.PendingOrderDTO;

public final class DashboardTransfer {
    private DashboardTransfer() {}

    public static DashboardVO toVO(DashboardDTO source) {
        return new DashboardVO(
                source.checkedInRooms(),
                source.pendingCheckInRooms(),
                source.availableRooms(),
                source.pendingOrders().stream().map(DashboardTransfer::toPendingVO).toList());
    }

    private static PendingOrderVO toPendingVO(PendingOrderDTO source) {
        return new PendingOrderVO(
                source.orderId(),
                source.orderNo(),
                source.bookerName(),
                source.roomCount(),
                source.roomTypeName(),
                source.plannedCheckinTime());
    }
}
