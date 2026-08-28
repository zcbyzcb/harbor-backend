package com.harbor.hotel.start.bootstrap;

import com.harbor.hotel.domain.booking.factory.BookingFactory;
import com.harbor.hotel.domain.inventory.factory.InventoryFactory;
import com.harbor.hotel.domain.inventory.repository.InventoryRepository;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class HotelTimeConfiguration {
    @Resource private InventoryRepository inventoryRepository;

    @Bean
    InventoryFactory inventoryFactory() {
        return new InventoryFactory(inventoryRepository);
    }

    @Bean
    BookingFactory bookingFactory(
            com.harbor.hotel.domain.booking.repository.BookingRepository bookings,
            Clock hotelClock,
            @Value("${hotel.inventory-window-days:7}") int days) {
        return new BookingFactory(
                bookings, inventoryRepository, hotelClock, days);
    }

    @Bean
    Clock hotelClock(@Value("${hotel.zone-id}") String zoneId) {
        return Clock.system(ZoneId.of(zoneId));
    }
}
