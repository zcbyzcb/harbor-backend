package com.harbor.hotel.infrastructure.persistence.repository;

import com.harbor.hotel.domain.inventory.repository.InventoryRepository;
import com.harbor.hotel.infrastructure.persistence.mapper.InventoryMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MybatisInventoryRepository implements InventoryRepository {
    @Resource private InventoryMapper inventoryMapper;

    @Override
    public boolean lockRoomType(Long id) {
        return inventoryMapper.lockRoomType(id) != null;
    }

    @Override
    public boolean hasCoveringOrder(Long id, LocalDate date) {
        return inventoryMapper.coveringOrders(id, date) > 0;
    }

    @Override
    public boolean isConsistent(Long id) {
        return inventoryMapper.isConsistent(id);
    }

    @Override
    public List<RoomTypeSeed> findActiveRoomTypes() {
        return inventoryMapper.findActiveRoomTypes().stream()
                .map(row -> new RoomTypeSeed(row.id()))
                .toList();
    }

    @Override
    public List<RoomSeed> findActiveRooms(Long roomTypeId) {
        return inventoryMapper.findActiveRooms(roomTypeId).stream()
                .map(
                        row ->
                                new RoomSeed(
                                        row.id(),
                                        row.roomNo(),
                                        !"OUT_OF_SERVICE".equals(row.physicalStatus())))
                .toList();
    }

    @Override
    public InventorySnapshot findByRoomTypeAndDate(Long roomTypeId, LocalDate stayDate) {
        InventoryMapper.InventoryPO row =
                inventoryMapper.findByRoomTypeAndDate(roomTypeId, stayDate);
        return row == null
                ? null
                : new InventorySnapshot(
                        row.id(),
                        row.totalRooms(),
                        row.bookedRooms(),
                        row.checkedInRooms(),
                        row.availableRooms(),
                        row.detailCount());
    }

    @Override
    public Long createInventory(Long roomTypeId, LocalDate stayDate, int totalRooms) {
        InventoryMapper.InventoryInsertPO row =
                new InventoryMapper.InventoryInsertPO(roomTypeId, stayDate, totalRooms, totalRooms);
        if (inventoryMapper.insertInventory(row) != 1)
            throw new IllegalStateException("inventory insert failed");
        return row.getId();
    }

    @Override
    public void createDetails(Long inventoryId, Long roomTypeId, List<RoomSeed> rooms) {
        List<InventoryMapper.RoomPO> rows =
                rooms.stream()
                        .map(
                                room ->
                                        new InventoryMapper.RoomPO(
                                                room.id(),
                                                room.roomNo(),
                                                room.active() ? "READY" : "OUT_OF_SERVICE"))
                        .toList();
        if (!rows.isEmpty()
                && inventoryMapper.insertDetails(inventoryId, roomTypeId, rows) != rows.size()) {
            throw new IllegalStateException("inventory detail insert failed");
        }
    }
}
