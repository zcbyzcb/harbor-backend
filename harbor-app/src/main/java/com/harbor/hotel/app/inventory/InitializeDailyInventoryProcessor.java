package com.harbor.hotel.app.inventory;

import com.harbor.hotel.domain.inventory.model.InventoryFactory;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitializeDailyInventoryProcessor {
    @Resource private InventoryFactory inventoryFactory;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public int process(InitializeDailyInventoryDTO command) {
        return inventoryFactory
                .createDailyInventory(command.roomTypeId(), command.stayDate())
                .initialize();
    }
}
