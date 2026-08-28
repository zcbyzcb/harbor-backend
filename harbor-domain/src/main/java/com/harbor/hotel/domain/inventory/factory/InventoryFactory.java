package com.harbor.hotel.domain.inventory.factory;

import com.harbor.hotel.domain.inventory.model.DailyInventory;
import com.harbor.hotel.domain.inventory.repository.InventoryRepository;

import java.time.LocalDate;
import java.util.Objects;

public final class InventoryFactory {
    private final InventoryRepository inventoryRepository;

    public InventoryFactory(InventoryRepository inventoryRepository) {
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
    }

    public DailyInventory createDailyInventory(Long roomTypeId, LocalDate stayDate) {
        return new DailyInventory(inventoryRepository, roomTypeId, stayDate);
    }
}
