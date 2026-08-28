package com.harbor.hotel.infrastructure.persistence.po;

public record LockPO(Long id, Long inventoryId, int roomCount, String status) {}
