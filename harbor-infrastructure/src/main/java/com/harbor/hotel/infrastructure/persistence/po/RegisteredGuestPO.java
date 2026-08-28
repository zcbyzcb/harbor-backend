package com.harbor.hotel.infrastructure.persistence.po;

public record RegisteredGuestPO(
        String roomId,
        String roomNo,
        String checkinTime,
        int guestSeq,
        String name,
        String phone) {}
