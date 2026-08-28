package com.harbor.hotel.api.web.dashboard;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.app.dashboard.qurier.GetHotelContextQurier;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;

@RestController
public class HotelContextController {
    @Resource private GetHotelContextQurier qurier;

    @GetMapping("/api/hotel-context")
    public ApiResponse<HotelContextVO> context() {
        var d = qurier.query();
        return ApiResponse.success(
                new HotelContextVO(
                        d.hotelDate(), d.serverTime(), d.lastCheckoutDate(), d.windowDays()));
    }

    public record HotelContextVO(
            String hotelDate, String serverTime, String lastCheckoutDate, int windowDays) {}
}
