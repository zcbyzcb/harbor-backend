package com.harbor.hotel.api.web.dashboard.vo;

public record HotelContextVO(
        String hotelDate, String serverTime, String lastCheckoutDate, int windowDays) {}
