package com.harbor.hotel.domain.inventory.model;

import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;

public final class InventoryState {
    private final Long id;
    private final Long roomTypeId;
    private final int totalRooms;
    private int bookedRooms;
    private int checkedInRooms;
    private int availableRooms;

    public InventoryState(
            Long id,
            Long roomTypeId,
            int totalRooms,
            int bookedRooms,
            int checkedInRooms,
            int availableRooms) {
        if (id == null
                || roomTypeId == null
                || totalRooms < 0
                || bookedRooms < 0
                || checkedInRooms < 0
                || availableRooms < 0
                || (long) bookedRooms + checkedInRooms + availableRooms != totalRooms) {
            throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
        }
        this.id = id;
        this.roomTypeId = roomTypeId;
        this.totalRooms = totalRooms;
        this.bookedRooms = bookedRooms;
        this.checkedInRooms = checkedInRooms;
        this.availableRooms = availableRooms;
    }

    public void reserve(int roomCount) {
        if (roomCount <= 0 || availableRooms < roomCount)
            throw new DomainException(ErrorCode.INVENTORY_NOT_AVAILABLE);
        bookedRooms += roomCount;
        availableRooms -= roomCount;
        verify();
    }

    public void cancelReservation(int roomCount) {
        if (roomCount <= 0 || bookedRooms < roomCount)
            throw new DomainException(ErrorCode.INVENTORY_STATE_CONFLICT);
        bookedRooms -= roomCount;
        availableRooms += roomCount;
        verify();
    }

    public void convertToCheckin(int roomCount) {
        if (roomCount <= 0 || bookedRooms < roomCount)
            throw new DomainException(ErrorCode.INVENTORY_STATE_CONFLICT);
        bookedRooms -= roomCount;
        checkedInRooms += roomCount;
        verify();
    }

    private void verify() {
        if ((long) bookedRooms + checkedInRooms + availableRooms != totalRooms)
            throw new DomainException(ErrorCode.INVENTORY_DATA_INCONSISTENT);
    }

    public Long id() {
        return id;
    }

    public Long roomTypeId() {
        return roomTypeId;
    }

    public int totalRooms() {
        return totalRooms;
    }

    public int bookedRooms() {
        return bookedRooms;
    }

    public int checkedInRooms() {
        return checkedInRooms;
    }

    public int availableRooms() {
        return availableRooms;
    }
}
