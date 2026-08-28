package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.RoomPO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RoomMapper {
    List<RoomPO> findActiveByRoomTypeId(@Param("roomTypeId") Long roomTypeId);
    List<RoomPO> findByIds(@Param("ids") List<Long> ids);
    int markOccupied(@Param("id") Long id);
}
