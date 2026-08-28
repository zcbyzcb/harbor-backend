package com.harbor.hotel.domain.booking.model;

public record Lock(Long id, Long inventoryId, int roomCount, String status) {}
