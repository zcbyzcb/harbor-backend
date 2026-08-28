package com.harbor.hotel.infrastructure.persistence.po;

import java.time.LocalDate;

public final class NewInventoryPO {
    private Long id;
    private final Long roomTypeId;
    private final LocalDate stayDate;
    private final int totalRooms;
    private final int availableRooms;
    public NewInventoryPO(Long roomTypeId, LocalDate stayDate, int totalRooms, int availableRooms) {
        this.roomTypeId = roomTypeId; this.stayDate = stayDate; this.totalRooms = totalRooms; this.availableRooms = availableRooms;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomTypeId() { return roomTypeId; }
    public LocalDate getStayDate() { return stayDate; }
    public int getTotalRooms() { return totalRooms; }
    public int getAvailableRooms() { return availableRooms; }
}
