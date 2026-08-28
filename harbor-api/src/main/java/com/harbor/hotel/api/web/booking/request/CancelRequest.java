package com.harbor.hotel.api.web.booking.request;

import jakarta.validation.constraints.Size;

public record CancelRequest(@Size(max = 500) String reason) {}
