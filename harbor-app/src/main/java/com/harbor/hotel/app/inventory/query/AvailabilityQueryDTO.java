package com.harbor.hotel.app.inventory.query;

import java.time.LocalDate;

public record AvailabilityQueryDTO(LocalDate checkinDate, LocalDate checkoutDate, int roomCount) {}
