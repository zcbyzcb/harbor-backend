package com.harbor.hotel.domain.inventory.repository;

import com.harbor.hotel.domain.inventory.model.InventorySnapshot;
import com.harbor.hotel.domain.inventory.model.RoomSeed;
import com.harbor.hotel.domain.inventory.model.RoomTypeSeed;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository {
    List<RoomTypeSeed> findActiveRoomTypes();

    boolean lockRoomType(Long roomTypeId);

    boolean hasCoveringOrder(Long roomTypeId, LocalDate stayDate);

    boolean isConsistent(Long inventoryId);

    List<RoomSeed> findActiveRooms(Long roomTypeId);

    InventorySnapshot findByRoomTypeAndDate(Long roomTypeId, LocalDate stayDate);

    Long createInventory(Long roomTypeId, LocalDate stayDate, int totalRooms);

    void createDetails(Long inventoryId, Long roomTypeId, List<RoomSeed> rooms);

}
