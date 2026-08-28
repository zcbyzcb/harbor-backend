package com.harbor.hotel.infrastructure.persistence.po;

public record InventoryPO(Long id, int totalRooms, int bookedRooms, int checkedInRooms,
        int availableRooms) {}
