package com.harbor.hotel.domain.booking.model;

public record Detail(
        Long id,
        Long inventoryId,
        Long roomTypeId,
        Long roomId,
        RoomInventoryDetailStatus status,
        int occupied,
        Long checkinId) {}
