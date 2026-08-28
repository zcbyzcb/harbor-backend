package com.harbor.hotel.domain.booking.model;

public record Checkin(Long id, Long roomId, Long employeeId, String requestId, byte[] requestHash) {}
