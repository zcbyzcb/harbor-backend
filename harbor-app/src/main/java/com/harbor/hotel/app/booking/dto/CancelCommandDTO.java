package com.harbor.hotel.app.booking.dto;

public record CancelCommandDTO(Long orderId, Long employeeId, String requestId, String reason) {}
