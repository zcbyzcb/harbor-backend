package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.InventoryPO;
import com.harbor.hotel.infrastructure.persistence.po.NewInventoryPO;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

public interface RoomTypeInventoryMapper {
    InventoryPO findByRoomTypeAndStayDate(@Param("roomTypeId") Long roomTypeId, @Param("stayDate") LocalDate stayDate);
    int insert(NewInventoryPO row);
    int reserve(@Param("id") Long id, @Param("roomCount") int roomCount);

    int cancelReservation(@Param("id") Long id, @Param("roomCount") int roomCount);

    int checkIn(@Param("id") Long id, @Param("roomCount") int roomCount);
    boolean isConsistent(@Param("inventoryId") Long inventoryId);
}
