package com.harbor.hotel.domain.inventory.repository;

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

    record RoomTypeSeed(Long id) {}

    record RoomSeed(Long id, String roomNo, boolean active) {}

    record InventorySnapshot(
            Long id,
            int totalRooms,
            int bookedRooms,
            int checkedInRooms,
            int availableRooms,
            int detailCount) {}
}
