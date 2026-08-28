package com.harbor.hotel.api.web.order.vo;

public record RegisteredRoomVO(
        String roomId,
        String roomNo,
        String checkinTime,
        java.util.List<RegisteredGuestVO> guests) {}
