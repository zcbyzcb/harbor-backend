package com.harbor.hotel.infrastructure.persistence.po;

import java.math.BigDecimal;

public record RoomTypePO(Long id, String name, BigDecimal price, int maxGuests) {}
