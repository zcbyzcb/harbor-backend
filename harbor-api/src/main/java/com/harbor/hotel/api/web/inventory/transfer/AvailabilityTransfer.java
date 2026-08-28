package com.harbor.hotel.api.web.inventory.transfer;

import com.harbor.hotel.api.web.inventory.vo.AvailableRoomTypeVO;
import com.harbor.hotel.app.inventory.dto.AvailableRoomTypeDTO;

public final class AvailabilityTransfer {
    private AvailabilityTransfer() {}

    public static AvailableRoomTypeVO toVO(AvailableRoomTypeDTO source) {
        return new AvailableRoomTypeVO(
                source.roomTypeId(),
                source.typeCode(),
                source.typeName(),
                source.bedType(),
                source.maxGuests(),
                source.nightlyPrice().toPlainString(),
                source.availableRooms(),
                source.inventoryReady(),
                source.totalAmount().toPlainString(),
                source.bookable());
    }
}
