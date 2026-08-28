package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.AvailableRoomTypePO;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityReadMapper {
    List<AvailableRoomTypePO> list(
            @Param("stayDates") List<LocalDate> stayDates, @Param("expectedDays") int expectedDays);
}
