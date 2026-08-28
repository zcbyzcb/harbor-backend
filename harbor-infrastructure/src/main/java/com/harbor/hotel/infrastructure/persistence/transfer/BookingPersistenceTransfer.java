package com.harbor.hotel.infrastructure.persistence.transfer;

import com.harbor.hotel.domain.booking.model.*;
import com.harbor.hotel.infrastructure.persistence.po.*;

public final class BookingPersistenceTransfer {
    private BookingPersistenceTransfer() {}

    public static RoomTypePO toPO(RoomType v) {
        return new RoomTypePO(v.id(), v.name(), v.price(), v.maxGuests());
    }

    public static RoomType toDomain(RoomTypePO v) {
        return v == null
                ? null
                : new RoomType(
                        v.id(), v.name(), v.price(), v.maxGuests());
    }

    public static OrderPO toPO(Order v) {
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
                v.status().name());
    }

    public static Order toDomain(OrderPO v) {
        return v == null
                ? null
                : new Order(
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
                        BookingOrderStatus.fromCode(v.status()));
    }

    public static NewOrderPO toPO(NewOrder v) {
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

    public static LockPO toPO(Lock v) {
        return new LockPO(v.id(), v.inventoryId(), v.roomCount(), v.status().code());
    }

    public static Lock toDomain(LockPO v) {
        return v == null
                ? null
                : new Lock(
                        v.id(), v.inventoryId(), v.roomCount(), InventoryLockStatus.fromCode(v.status()));
    }

    public static RoomPO toPO(Room v) {
        return new RoomPO(v.id(), v.roomTypeId(), v.roomNo(), v.physicalStatus().name());
    }

    public static Room toDomain(RoomPO v) {
        return v == null
                ? null
                : new Room(
                        v.id(), v.roomTypeId(), v.roomNo(), RoomPhysicalStatus.fromCode(v.physicalStatus()));
    }

    public static DetailPO toPO(Detail v) {
        return new DetailPO(
                v.id(),
                v.inventoryId(),
                v.roomTypeId(),
                v.roomId(),
                v.status().name(),
                v.occupied(),
                v.checkinId());
    }

    public static Detail toDomain(DetailPO v) {
        return v == null
                ? null
                : new Detail(
                        v.id(),
                        v.inventoryId(),
                        v.roomTypeId(),
                        v.roomId(),
                        RoomInventoryDetailStatus.fromCode(v.status()),
                        v.occupied(),
                        v.checkinId());
    }

    public static CheckinPO toPO(Checkin v) {
        return new CheckinPO(v.id(), v.roomId(), v.employeeId(), v.requestId(), v.requestHash());
    }

    public static Checkin toDomain(CheckinPO v) {
        return v == null
                ? null
                : new Checkin(
                        v.id(), v.roomId(), v.employeeId(), v.requestId(), v.requestHash());
    }

    public static NewCheckinPO toPO(NewCheckin v) {
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
