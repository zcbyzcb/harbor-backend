package com.harbor.hotel.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryMapper {
    List<RoomTypePO> findActiveRoomTypes();

    Long lockRoomType(@Param("roomTypeId") Long roomTypeId);

    int coveringOrders(@Param("roomTypeId") Long roomTypeId, @Param("stayDate") LocalDate stayDate);

    boolean isConsistent(@Param("inventoryId") Long inventoryId);

    List<RoomPO> findActiveRooms(@Param("roomTypeId") Long roomTypeId);

    InventoryPO findByRoomTypeAndDate(
            @Param("roomTypeId") Long roomTypeId, @Param("stayDate") LocalDate stayDate);

    int insertInventory(InventoryInsertPO row);

    int insertDetails(
            @Param("inventoryId") Long inventoryId,
            @Param("roomTypeId") Long roomTypeId,
            @Param("rooms") List<RoomPO> rooms);

    record RoomTypePO(Long id) {}

    record RoomPO(Long id, String roomNo, String physicalStatus) {}

    record InventoryPO(
            Long id,
            int totalRooms,
            int bookedRooms,
            int checkedInRooms,
            int availableRooms,
            int detailCount) {}

    final class InventoryInsertPO {
        private Long id;
        private final Long roomTypeId;
        private final LocalDate stayDate;
        private final int totalRooms;
        private final int availableRooms;

        public InventoryInsertPO(
                Long roomTypeId, LocalDate stayDate, int totalRooms, int availableRooms) {
            this.roomTypeId = roomTypeId;
            this.stayDate = stayDate;
            this.totalRooms = totalRooms;
            this.availableRooms = availableRooms;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getRoomTypeId() {
            return roomTypeId;
        }

        public LocalDate getStayDate() {
            return stayDate;
        }

        public int getTotalRooms() {
            return totalRooms;
        }

        public int getAvailableRooms() {
            return availableRooms;
        }
    }
}
