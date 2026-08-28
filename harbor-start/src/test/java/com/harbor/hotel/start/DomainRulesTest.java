package com.harbor.hotel.start;

import static org.junit.jupiter.api.Assertions.*;

import com.harbor.hotel.domain.inventory.model.InventoryState;
import com.harbor.hotel.domain.shared.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DomainRulesTest {
    @Test
    void inventoryConservesAcrossTransitions() {
        var i = new InventoryState(1L, 1L, 4, 0, 0, 4);
        i.reserve(3);
        i.convertToCheckin(2);
        i.cancelReservation(1);
        assertEquals(0, i.bookedRooms());
        assertEquals(2, i.checkedInRooms());
        assertEquals(2, i.availableRooms());
    }

    @Test
    void oversellAndNegativeActionsAreRejected() {
        var i = new InventoryState(1L, 1L, 1, 0, 0, 1);
        assertThrows(DomainException.class, () -> i.reserve(2));
        assertThrows(DomainException.class, () -> i.reserve(-1));
        assertThrows(DomainException.class, () -> i.cancelReservation(1));
        assertThrows(DomainException.class, () -> i.convertToCheckin(1));
    }

    @Test
    void invalidSnapshotIsRejected() {
        assertThrows(DomainException.class, () -> new InventoryState(1L, 1L, 1, 1, 1, 0));
    }

    @Test
    void checkoutIsExclusive() {
        assertEquals(
                2, new StayPeriod(LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 30)).nights());
        assertThrows(
                DomainException.class,
                () -> new StayPeriod(LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 28)));
    }
}
