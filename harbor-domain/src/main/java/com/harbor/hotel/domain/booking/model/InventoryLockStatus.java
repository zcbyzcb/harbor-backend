package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.shared.DomainException;

public enum InventoryLockStatus {
    LOCKED("locked"), RELEASED("release"), CANCELLED("cancel");
    private final String code;
    InventoryLockStatus(String code) { this.code = code; }
    public String code() { return code; }
    public static InventoryLockStatus fromCode(String code) {
        for (InventoryLockStatus value : values()) if (value.code.equals(code)) return value;
        throw new DomainException("INVENTORY_DATA_INCONSISTENT");
    }
}
