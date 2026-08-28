package com.harbor.hotel.app.booking.dto;

import java.util.List;

public record CheckInCommandDTO(
        Long orderId, Long employeeId, String requestId, List<AllocationDTO> rooms) {
    public record AllocationDTO(Long roomId, List<GuestDTO> guests) {}

    public record GuestDTO(String name, String phone, String identityNo) {}
}
