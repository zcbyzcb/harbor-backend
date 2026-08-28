package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.PendingOrderPO;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface DashboardReadMapper {
    int missingInventories(@Param("today") LocalDate today);

    int countCheckedInRooms(@Param("today") LocalDate today);

    int countPendingCheckInRooms(@Param("today") LocalDate today);

    int countAvailableRooms(@Param("today") LocalDate today);

    List<PendingOrderPO> listPendingOrders(@Param("today") LocalDate today);
}
