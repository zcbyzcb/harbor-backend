package com.harbor.hotel.api.web.booking.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingRequest(
        @NotNull @Positive Long roomTypeId,
        @NotNull LocalDate checkinDate,
        @NotNull LocalDate checkoutDate,
        @Min(1) @Max(65535) int roomCount,
        @NotBlank @Size(max = 64) String bookerName,
        @NotBlank @Size(min = 11, max = 11) @Pattern(regexp = "^1[3-9]\\d{9}$") String bookerPhone,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal confirmedPrice,
        @Size(max = 500) String remark) {}
