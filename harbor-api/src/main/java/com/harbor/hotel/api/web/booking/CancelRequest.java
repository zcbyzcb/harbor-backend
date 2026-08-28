package com.harbor.hotel.api.web.booking;

import jakarta.validation.constraints.Size;

public record CancelRequest(@Size(max = 500) String reason) {}
