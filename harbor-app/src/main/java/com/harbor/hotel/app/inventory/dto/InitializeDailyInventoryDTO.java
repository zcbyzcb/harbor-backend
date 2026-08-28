package com.harbor.hotel.app.inventory.dto;

import java.time.LocalDate;

public record InitializeDailyInventoryDTO(Long roomTypeId, LocalDate stayDate) {}
