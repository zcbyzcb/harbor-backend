package com.harbor.hotel.domain.booking.model;

import java.util.List;

public record Allocation(Long roomId, List<Guest> guests) {}
