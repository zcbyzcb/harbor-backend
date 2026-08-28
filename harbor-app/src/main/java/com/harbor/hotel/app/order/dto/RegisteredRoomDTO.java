package com.harbor.hotel.app.order.dto;

public record RegisteredRoomDTO(
        String roomId,
        String roomNo,
        String checkinTime,
        java.util.List<RegisteredGuestDTO> guests) {}
