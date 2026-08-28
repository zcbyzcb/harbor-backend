package com.harbor.hotel.domain.booking.validator;

import com.harbor.hotel.domain.booking.model.Allocation;
import com.harbor.hotel.domain.booking.model.Guest;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;
import java.util.List;

public final class CheckInValidator {
    private CheckInValidator() {}
    public static void validateStructure(List<Allocation> allocations) {
        if (allocations == null || allocations.isEmpty()
                || allocations.stream().anyMatch(a -> a == null || a.roomId() == null || a.guests() == null))
            throw new DomainException(ErrorCode.INVALID_ARGUMENT);
    }
    public static void validateAllocations(List<Allocation> allocations, int roomCount, int maxGuests) {
        validateStructure(allocations);
        if (allocations.size() != roomCount
                || allocations.stream().map(Allocation::roomId).distinct().count() != roomCount)
            throw new DomainException(ErrorCode.ROOM_COUNT_MISMATCH);
        for (Allocation allocation : allocations) {
            if (allocation.guests().isEmpty() || allocation.guests().size() > maxGuests
                    || allocation.guests().stream().anyMatch(CheckInValidator::invalidGuest))
                throw new DomainException(ErrorCode.INVALID_GUESTS);
        }
    }
    private static boolean invalidGuest(Guest guest) {
        return guest == null || guest.name() == null || guest.name().isBlank()
                || guest.name().length() > 64 || (guest.phone() != null && !guest.phone().isEmpty()
                && !guest.phone().matches("[+0-9][0-9 -]{5,31}"));
    }
}
