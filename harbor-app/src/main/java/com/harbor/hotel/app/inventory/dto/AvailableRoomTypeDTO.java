package com.harbor.hotel.app.inventory.dto;

import java.math.BigDecimal;

public record AvailableRoomTypeDTO(
        String roomTypeId,
        String typeCode,
        String typeName,
        String bedType,
        int maxGuests,
        BigDecimal nightlyPrice,
        int availableRooms,
        boolean inventoryReady,
        BigDecimal totalAmount,
        boolean bookable) {}
