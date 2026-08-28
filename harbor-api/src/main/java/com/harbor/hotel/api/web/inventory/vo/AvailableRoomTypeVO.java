package com.harbor.hotel.api.web.inventory.vo;

public record AvailableRoomTypeVO(
        String roomTypeId,
        String typeCode,
        String typeName,
        String bedType,
        int maxGuests,
        String nightlyPrice,
        int availableRooms,
        boolean inventoryReady,
        String totalAmount,
        boolean bookable) {}
