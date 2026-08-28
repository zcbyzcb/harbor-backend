package com.harbor.hotel.domain.inventory.model;

import com.harbor.hotel.domain.inventory.repository.InventoryRepository;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;

import java.time.LocalDate;

public final class DailyInventory {
    private final InventoryRepository inventoryRepository;
    private final Long roomTypeId;
    private final LocalDate stayDate;

    public DailyInventory(InventoryRepository inventoryRepository, Long roomTypeId, LocalDate stayDate) {
        if (roomTypeId == null || stayDate == null) {
            throw new DomainException(ErrorCode.INVALID_ARGUMENT);
        }
        this.inventoryRepository = inventoryRepository;
        this.roomTypeId = roomTypeId;
        this.stayDate = stayDate;
    }

    public int initialize() {
        if (!inventoryRepository.lockRoomType(roomTypeId))
            throw new DomainException(ErrorCode.ROOM_TYPE_NOT_FOUND);
        java.util.List<RoomSeed> rooms =
                inventoryRepository.findActiveRooms(roomTypeId);
        if (rooms.isEmpty()) {
            throw new DomainException(ErrorCode.ROOM_TYPE_NOT_FOUND);
        }
        InventorySnapshot existing =
                inventoryRepository.findByRoomTypeAndDate(roomTypeId, stayDate);
        int activeRoomCount =
                Math.toIntExact(
                        rooms.stream().filter(RoomSeed::active).count());
        if (existing != null) {
            if (!inventoryRepository.isConsistent(existing.id())
                    || existing.detailCount() != rooms.size()
                    || (long) existing.bookedRooms()
                                    + existing.checkedInRooms()
                                    + existing.availableRooms()
                            != existing.totalRooms()) {
                throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
            }
            return 0;
        }
        if (inventoryRepository.hasCoveringOrder(roomTypeId, stayDate))
            throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
        Long inventoryId =
                inventoryRepository.createInventory(roomTypeId, stayDate, activeRoomCount);
        inventoryRepository.createDetails(inventoryId, roomTypeId, rooms);
        return 1;
    }
}
