package com.harbor.hotel.api.web.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CheckInRequest(@NotEmpty @Size(max = 65535) List<@NotNull @Valid RoomRequest> rooms) {
    public record RoomRequest(
            @NotNull @Positive Long roomId,
            @NotEmpty @Size(max = 20) List<@NotNull @Valid GuestRequest> guests) {}

    public record GuestRequest(
            @NotBlank @Size(max = 64) String name, @Size(max = 32) String phone) {}
}
