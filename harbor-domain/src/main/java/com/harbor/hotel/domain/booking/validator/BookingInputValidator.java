package com.harbor.hotel.domain.booking.validator;

import com.harbor.hotel.domain.booking.model.BookingInput;
import com.harbor.hotel.domain.shared.DomainException;

public final class BookingInputValidator {
    private BookingInputValidator() {}

    public static void validate(BookingInput input, String orderNo) {
        if (input.roomTypeId() == null || input.roomTypeId() <= 0 || input.roomCount() < 1
                || input.roomCount() > 65535 || input.confirmedPrice() == null
                || input.confirmedPrice().signum() < 0 || input.confirmedPrice().scale() > 2
                || input.bookerName() == null || input.bookerName().isBlank()
                || input.bookerPhone() == null
                || !input.bookerPhone().matches("[+0-9][0-9 -]{5,31}")) {
            throw new DomainException("INVALID_ARGUMENT");
        }
        if (orderNo == null || !orderNo.matches("UO\\d{18}")) {
            throw new DomainException("INVALID_ORDER_NO");
        }
    }
}
