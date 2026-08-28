package com.harbor.hotel.app.dashboard.qurier;

import com.harbor.hotel.app.dashboard.dto.DashboardDTO;
import com.harbor.hotel.app.dashboard.transfer.PendingOrderReadTransfer;
import com.harbor.hotel.infrastructure.persistence.mapper.DashboardReadMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Component
public class GetDashboardQurier {
    @Resource
    private DashboardReadMapper dashboardReadMapper;
    @Resource
    private Clock clock;

    @Transactional(
            readOnly = true,
            isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public DashboardDTO query() {
        LocalDate today = LocalDate.now(clock);
        if (dashboardReadMapper.missingInventories(today) > 0)
            throw new com.harbor.hotel.domain.shared.DomainException("INVENTORY_NOT_READY");
        return new DashboardDTO(
                dashboardReadMapper.countCheckedInRooms(today),
                dashboardReadMapper.countPendingCheckInRooms(today),
                dashboardReadMapper.countAvailableRooms(today),
                dashboardReadMapper.listPendingOrders(today).stream()
                        .map(PendingOrderReadTransfer::toDTO)
                        .toList());
    }
}
