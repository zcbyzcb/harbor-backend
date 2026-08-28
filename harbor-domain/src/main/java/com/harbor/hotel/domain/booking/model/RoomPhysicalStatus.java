package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.shared.DomainException;

public enum RoomPhysicalStatus {
    READY, OCCUPIED, DIRTY, OUT_OF_SERVICE;
    public static RoomPhysicalStatus fromCode(String code) {
        try { return valueOf(code); } catch (IllegalArgumentException | NullPointerException exception) { throw new DomainException("INVENTORY_DATA_INCONSISTENT"); }
    }
}
