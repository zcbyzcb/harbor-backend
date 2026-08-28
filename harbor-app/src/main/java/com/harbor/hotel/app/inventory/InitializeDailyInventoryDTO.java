package com.harbor.hotel.app.inventory;

import java.time.LocalDate;

public record InitializeDailyInventoryDTO(Long roomTypeId, LocalDate stayDate) {}
