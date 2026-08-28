package com.harbor.hotel.app.dashboard.dto;

public record HotelContextDTO(
        String hotelDate, String serverTime, String lastCheckoutDate, int windowDays) {}
