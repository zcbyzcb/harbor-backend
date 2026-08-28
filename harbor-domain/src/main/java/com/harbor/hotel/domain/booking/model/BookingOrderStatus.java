package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;

public enum BookingOrderStatus {
    PENDING,
    CHECKED_IN,
    CANCELLED;

    public static BookingOrderStatus fromCode(String code) {
        try { return valueOf(code); } catch (IllegalArgumentException | NullPointerException exception) {
            throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
        }
    }
}
