package com.harbor.hotel.infrastructure.persistence.po;

import java.math.BigDecimal;

public record AvailableRoomTypePO(
        String roomTypeId,
        String typeCode,
        String typeName,
        String bedType,
        int maxGuests,
        BigDecimal nightlyPrice,
        int availableRooms,
        boolean inventoryReady) {}
