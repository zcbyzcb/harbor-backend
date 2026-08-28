package com.harbor.hotel.app.dashboard.qurier;

import com.harbor.hotel.app.dashboard.dto.HotelContextDTO;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class GetHotelContextQurier {
    @Resource
    private Clock clock;

    @Value("${hotel.inventory-window-days:7}")
    private int days;

    public HotelContextDTO query() {
        ZonedDateTime now = ZonedDateTime.now(clock);
        return new HotelContextDTO(
                now.toLocalDate().toString(),
                now.toOffsetDateTime().toString(),
                now.toLocalDate().plusDays(days).toString(),
                days);
    }
}
