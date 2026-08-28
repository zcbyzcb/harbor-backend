package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.NewOrderPO;
import com.harbor.hotel.infrastructure.persistence.po.OrderPO;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingOrderMapper {
    OrderPO findRequest(@Param("employeeId") Long employeeId, @Param("key") String key);
    Long orderType(@Param("id") Long id);
    OrderPO lockOrder(@Param("id") Long id);
    int insert(NewOrderPO row);
    int markCheckedIn(@Param("id") Long id);
    int markCancelled(@Param("id") Long id, @Param("employeeId") Long employeeId,
            @Param("now") LocalDateTime now, @Param("reason") String reason);
    int coveringOrders(@Param("roomTypeId") Long roomTypeId, @Param("stayDate") LocalDate stayDate);
}
