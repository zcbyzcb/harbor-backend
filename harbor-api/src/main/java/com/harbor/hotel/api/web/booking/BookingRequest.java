package com.harbor.hotel.api.web.booking;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingRequest(
        @NotNull @Positive Long roomTypeId,
        @NotNull LocalDate checkinDate,
        @NotNull LocalDate checkoutDate,
        @Min(1) @Max(65535) int roomCount,
        @NotBlank @Size(max = 64) String bookerName,
        @NotBlank @Size(max = 32) String bookerPhone,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal confirmedPrice,
        @Size(max = 500) String remark) {}
