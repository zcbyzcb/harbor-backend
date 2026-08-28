package com.harbor.hotel.domain.inventory.model;

public record InventorySnapshot(
        Long id,
        int totalRooms,
        int bookedRooms,
        int checkedInRooms,
        int availableRooms,
        int detailCount) {}
