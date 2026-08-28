package com.harbor.hotel.domain.shared;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record StayPeriod(LocalDate checkinDate, LocalDate checkoutDate) {
    public StayPeriod {
        if (checkinDate == null || checkoutDate == null || !checkoutDate.isAfter(checkinDate)) {
            throw new DomainException(ErrorCode.INVALID_STAY_PERIOD);
        }
    }

    public int nights() {
        return Math.toIntExact(ChronoUnit.DAYS.between(checkinDate, checkoutDate));
    }
}
