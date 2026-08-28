package com.harbor.hotel.app.inventory.transfer;

import com.harbor.hotel.app.inventory.dto.AvailableRoomTypeDTO;
import com.harbor.hotel.infrastructure.persistence.po.AvailableRoomTypePO;

public final class AvailableRoomTypeReadTransfer {
    private AvailableRoomTypeReadTransfer() {}

    public static AvailableRoomTypeDTO toDTO(AvailableRoomTypePO p, int nights, int count) {
        return p == null
                ? null
                : new AvailableRoomTypeDTO(
                        p.roomTypeId(),
                        p.typeCode(),
                        p.typeName(),
                        p.bedType(),
                        p.maxGuests(),
                        p.nightlyPrice(),
                        p.availableRooms(),
                        p.inventoryReady(),
                        p.nightlyPrice()
                                .multiply(java.math.BigDecimal.valueOf((long) nights * count)),
                        p.inventoryReady() && p.availableRooms() >= count);
    }
}
