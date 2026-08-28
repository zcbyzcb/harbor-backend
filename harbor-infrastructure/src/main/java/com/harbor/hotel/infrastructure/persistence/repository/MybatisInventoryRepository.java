package com.harbor.hotel.infrastructure.persistence.repository;

import com.harbor.hotel.domain.inventory.repository.InventoryRepository;
import com.harbor.hotel.domain.inventory.model.InventorySnapshot;
import com.harbor.hotel.domain.inventory.model.RoomSeed;
import com.harbor.hotel.domain.inventory.model.RoomTypeSeed;
import com.harbor.hotel.infrastructure.persistence.mapper.*;
import com.harbor.hotel.infrastructure.persistence.po.*;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MybatisInventoryRepository implements InventoryRepository {
    @Resource
    private RoomTypeMapper roomTypeMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private BookingOrderMapper bookingOrderMapper;
    @Resource
    private RoomTypeInventoryMapper roomTypeInventoryMapper;
    @Resource
    private RoomInventoryDetailMapper roomInventoryDetailMapper;

    @Override
    public boolean lockRoomType(Long id) {
        return roomTypeMapper.findById(id) != null;
    }

    @Override
    public boolean hasCoveringOrder(Long id, LocalDate date) {
        return bookingOrderMapper.coveringOrders(id, date) > 0;
    }

    @Override
    public boolean isConsistent(Long id) {
        return roomTypeInventoryMapper.isConsistent(id);
    }

    @Override
    public List<RoomTypeSeed> findActiveRoomTypes() {
        return roomTypeMapper.findActive().stream()
                .map(row -> new RoomTypeSeed(row.id()))
                .toList();
    }

    @Override
    public List<RoomSeed> findActiveRooms(Long roomTypeId) {
        return roomMapper.findActiveByRoomTypeId(roomTypeId).stream()
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
        InventoryPO row = roomTypeInventoryMapper.findByRoomTypeAndStayDate(roomTypeId, stayDate);
        return row == null
                ? null
                : new InventorySnapshot(
                        row.id(),
                        row.totalRooms(),
                        row.bookedRooms(),
                        row.checkedInRooms(),
                        row.availableRooms(),
                        roomInventoryDetailMapper.countByInventoryId(row.id()));
    }

    @Override
    public Long createInventory(Long roomTypeId, LocalDate stayDate, int totalRooms) {
        NewInventoryPO row = new NewInventoryPO(roomTypeId, stayDate, totalRooms, totalRooms);
        if (roomTypeInventoryMapper.insert(row) != 1)
            throw new IllegalStateException("inventory insert failed");
        return row.getId();
    }

    @Override
    public void createDetails(Long inventoryId, Long roomTypeId, List<RoomSeed> rooms) {
        List<InventoryRoomPO> rows =
                rooms.stream()
                        .map(
                                room ->
                                        new InventoryRoomPO(
                                                room.id(),
                                                room.roomNo(),
                                                room.active() ? "READY" : "OUT_OF_SERVICE"))
                        .toList();
        if (!rows.isEmpty()
                && roomInventoryDetailMapper.insertBatch(inventoryId, roomTypeId, rows) != rows.size()) {
            throw new IllegalStateException("inventory detail insert failed");
        }
    }
}
