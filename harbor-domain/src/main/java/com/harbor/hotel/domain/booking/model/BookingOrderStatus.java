package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.shared.DomainException;

public enum BookingOrderStatus {
    PENDING,
    CHECKED_IN,
    CANCELLED;

    public static BookingOrderStatus fromCode(String code) {
        try { return valueOf(code); } catch (IllegalArgumentException | NullPointerException exception) {
            throw new DomainException("INVENTORY_DATA_INCONSISTENT");
        }
    }
}
