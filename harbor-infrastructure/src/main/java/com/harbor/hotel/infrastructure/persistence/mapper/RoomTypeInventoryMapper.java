package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.InventoryPO;
import com.harbor.hotel.infrastructure.persistence.po.NewInventoryPO;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

public interface RoomTypeInventoryMapper {
    InventoryPO lockByRoomTypeAndStayDate(@Param("roomTypeId") Long roomTypeId, @Param("stayDate") LocalDate stayDate);
    int insert(NewInventoryPO row);
    int updateCounts(@Param("id") Long id, @Param("booked") int booked,
            @Param("checkedIn") int checkedIn, @Param("available") int available);
    boolean isConsistent(@Param("inventoryId") Long inventoryId);
}
