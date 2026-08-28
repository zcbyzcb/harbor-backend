package com.harbor.hotel.infrastructure.persistence.po;

public record DetailPO(
        Long id,
        Long inventoryId,
        Long roomTypeId,
        Long roomId,
        String status,
        int occupied,
        Long checkinId) {}
