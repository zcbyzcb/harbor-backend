package com.harbor.hotel.infrastructure.persistence.po;

public record CheckinPO(
        Long id, Long roomId, Long employeeId, String requestId, byte[] requestHash) {}
