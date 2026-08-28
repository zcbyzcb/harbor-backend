package com.harbor.hotel.infrastructure.persistence.transfer;

import com.harbor.hotel.domain.booking.repository.BookingRepository;
import com.harbor.hotel.infrastructure.persistence.po.*;

public final class BookingPersistenceTransfer {
    private BookingPersistenceTransfer() {}

    public static RoomTypePO toPO(BookingRepository.RoomType v) {
        return new RoomTypePO(v.id(), v.name(), v.price(), v.maxGuests());
    }

    public static BookingRepository.RoomType toDomain(RoomTypePO v) {
        return v == null
                ? null
                : new BookingRepository.RoomType(v.id(), v.name(), v.price(), v.maxGuests());
    }

    public static OrderPO toPO(BookingRepository.Order v) {
        return new OrderPO(
                v.id(),
                v.orderNo(),
                v.employeeId(),
                v.requestId(),
                v.requestHash(),
                v.roomTypeId(),
                v.roomCount(),
                v.checkinTime(),
                v.checkoutTime(),
                v.nights(),
                v.status());
    }

    public static BookingRepository.Order toDomain(OrderPO v) {
        return v == null
                ? null
                : new BookingRepository.Order(
                        v.id(),
                        v.orderNo(),
                        v.employeeId(),
                        v.requestId(),
                        v.requestHash(),
                        v.roomTypeId(),
                        v.roomCount(),
                        v.checkinTime(),
                        v.checkoutTime(),
                        v.nights(),
                        v.status());
    }

    public static NewOrderPO toPO(BookingRepository.NewOrder v) {
        return new NewOrderPO(
                v.orderNo(),
                v.employeeId(),
                v.requestId(),
                v.requestHash(),
                v.roomTypeId(),
                v.roomTypeName(),
                v.roomCount(),
                v.bookerName(),
                v.bookerPhone(),
                v.checkinTime(),
                v.checkoutTime(),
                v.nights(),
                v.nightlyPrice(),
                v.totalAmount(),
                v.remark());
    }

    public static LockPO toPO(BookingRepository.Lock v) {
        return new LockPO(v.id(), v.inventoryId(), v.roomCount(), v.status());
    }

    public static BookingRepository.Lock toDomain(LockPO v) {
        return v == null
                ? null
                : new BookingRepository.Lock(v.id(), v.inventoryId(), v.roomCount(), v.status());
    }

    public static RoomPO toPO(BookingRepository.Room v) {
        return new RoomPO(v.id(), v.roomTypeId(), v.roomNo(), v.physicalStatus());
    }

    public static BookingRepository.Room toDomain(RoomPO v) {
        return v == null
                ? null
                : new BookingRepository.Room(
                        v.id(), v.roomTypeId(), v.roomNo(), v.physicalStatus());
    }

    public static DetailPO toPO(BookingRepository.Detail v) {
        return new DetailPO(
                v.id(),
                v.inventoryId(),
                v.roomTypeId(),
                v.roomId(),
                v.status(),
                v.occupied(),
                v.checkinId());
    }

    public static BookingRepository.Detail toDomain(DetailPO v) {
        return v == null
                ? null
                : new BookingRepository.Detail(
                        v.id(),
                        v.inventoryId(),
                        v.roomTypeId(),
                        v.roomId(),
                        v.status(),
                        v.occupied(),
                        v.checkinId());
    }

    public static CheckinPO toPO(BookingRepository.Checkin v) {
        return new CheckinPO(v.id(), v.roomId(), v.employeeId(), v.requestId(), v.requestHash());
    }

    public static BookingRepository.Checkin toDomain(CheckinPO v) {
        return v == null
                ? null
                : new BookingRepository.Checkin(
                        v.id(), v.roomId(), v.employeeId(), v.requestId(), v.requestHash());
    }

    public static NewCheckinPO toPO(BookingRepository.NewCheckin v) {
        return new NewCheckinPO(
                v.orderId(),
                v.roomTypeId(),
                v.roomId(),
                v.roomNo(),
                v.now(),
                v.employeeId(),
                v.requestId(),
                v.requestHash());
    }
}
