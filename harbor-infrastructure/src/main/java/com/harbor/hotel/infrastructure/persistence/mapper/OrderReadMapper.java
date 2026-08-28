package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.*;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderReadMapper {
    List<OrderSummaryPO> page(OrderSearchPO query);

    long count(OrderSearchPO query);

    OrderSummaryPO detail(@Param("id") Long id);

    List<RegisteredGuestPO> registeredGuests(@Param("id") Long id);

    List<RoomCandidatePO> availableRooms(@Param("id") Long id);

    int missingInventory(@Param("id") Long id);
}
