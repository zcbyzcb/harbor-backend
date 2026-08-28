package com.harbor.hotel.api.web.dashboard.vo;

import java.util.List;

public record DashboardVO(
        int checkedInRooms,
        int pendingCheckInRooms,
        int availableRooms,
        List<PendingOrderVO> pendingOrders) {}
