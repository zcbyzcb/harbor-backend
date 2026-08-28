package com.harbor.hotel.domain.booking.model;

import java.math.BigDecimal;

public record RoomType(Long id, String name, BigDecimal price, int maxGuests) {}
