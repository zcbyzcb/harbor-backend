package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.DetailPO;
import com.harbor.hotel.infrastructure.persistence.po.InventoryRoomPO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RoomInventoryDetailMapper {
    int insertBatch(@Param("inventoryId") Long inventoryId, @Param("roomTypeId") Long roomTypeId,
            @Param("rooms") List<InventoryRoomPO> rooms);
    List<DetailPO> lockByInventoryIdsAndRoomIds(@Param("inventoryIds") List<Long> inventoryIds,
            @Param("roomIds") List<Long> roomIds);
    int markOccupied(@Param("id") Long id, @Param("checkinId") Long checkinId);
}
