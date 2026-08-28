package com.harbor.hotel.app.dashboard.dto;

import java.util.List;

public record DashboardDTO(
        int checkedInRooms,
        int pendingCheckInRooms,
        int availableRooms,
        List<PendingOrderDTO> pendingOrders) {}
