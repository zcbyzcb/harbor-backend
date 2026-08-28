package com.harbor.hotel.infrastructure.persistence.repository;

import com.harbor.hotel.domain.booking.repository.BookingRepository;
import com.harbor.hotel.domain.booking.model.*;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.infrastructure.persistence.mapper.*;
import com.harbor.hotel.infrastructure.persistence.transfer.BookingPersistenceTransfer;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MybatisBookingRepository implements BookingRepository {
    @Resource
    private BookingOrderMapper bookingOrderMapper;
    @Resource
    private RoomTypeMapper roomTypeMapper;
    @Resource
    private InventoryLockMapper inventoryLockMapper;
    @Resource
    private RoomTypeInventoryMapper roomTypeInventoryMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private RoomInventoryDetailMapper roomInventoryDetailMapper;
    @Resource
    private CheckinMapper checkinMapper;
    @Resource
    private OrderOperationLogMapper orderOperationLogMapper;

    private void one(int affected) {
        if (affected != 1) throw new DomainException("INVENTORY_STATE_CONFLICT");
    }

    public RoomType lockType(Long id) {
        return BookingPersistenceTransfer.toDomain(roomTypeMapper.findById(id));
    }

    public Order findRequest(Long employeeId, String key) {
        return BookingPersistenceTransfer.toDomain(bookingOrderMapper.findRequest(employeeId, key));
    }

    public Long orderType(Long id) {
        return bookingOrderMapper.orderType(id);
    }

    public Order lockOrder(Long id) {
        return BookingPersistenceTransfer.toDomain(bookingOrderMapper.findById(id));
    }

    public Long insertOrder(NewOrder order) {
        com.harbor.hotel.infrastructure.persistence.po.NewOrderPO row =
                BookingPersistenceTransfer.toPO(order);
        one(bookingOrderMapper.insert(row));
        return row.getId();
    }

    public List<Lock> lockReservations(Long id) {
        return inventoryLockMapper.findByOrderId(id).stream()
                .map(BookingPersistenceTransfer::toDomain)
                .toList();
    }

    public void insertReservation(Long orderId, Long inventoryId, int count) {
        one(inventoryLockMapper.insert(orderId, inventoryId, count));
    }

    public void updateInventory(Long id, int booked, int checkedIn, int available,
            int expectedBooked, int expectedCheckedIn, int expectedAvailable) {
        one(roomTypeInventoryMapper.updateCounts(id, booked, checkedIn, available,
                expectedBooked, expectedCheckedIn, expectedAvailable));
    }

    public void transitionReservation(Long id, String status) {
        one(inventoryLockMapper.transition(id, status));
    }

    public List<Room> lockRooms(List<Long> ids) {
        return roomMapper.findByIds(ids).stream().map(BookingPersistenceTransfer::toDomain).toList();
    }

    public List<Detail> lockDetails(List<Long> inventories, List<Long> rooms) {
        return roomInventoryDetailMapper.findByInventoryIdsAndRoomIds(inventories, rooms).stream()
                .map(BookingPersistenceTransfer::toDomain)
                .toList();
    }

    public List<Checkin> checkins(Long id) {
        return checkinMapper.findByOrderId(id).stream().map(BookingPersistenceTransfer::toDomain).toList();
    }

    public Long insertCheckin(NewCheckin checkin) {
        com.harbor.hotel.infrastructure.persistence.po.NewCheckinPO row =
                BookingPersistenceTransfer.toPO(checkin);
        one(checkinMapper.insert(row));
        return row.getId();
    }

    public void insertGuest(Long id, int seq, Guest guest) {
        one(checkinMapper.insertGuest(id, seq, guest.name(), guest.phone()));
    }

    public void occupyDetail(Long id, Long checkinId) {
        one(roomInventoryDetailMapper.markOccupied(id, checkinId));
    }

    public void occupyRoom(Long id) {
        one(roomMapper.markOccupied(id));
    }

    public void markCheckedIn(Long id) {
        one(bookingOrderMapper.markCheckedIn(id));
    }

    public void markCancelled(Long id, Long employeeId, LocalDateTime now, String reason) {
        one(bookingOrderMapper.markCancelled(id, employeeId, now, reason));
    }

    public void audit(
            Long id,
            String operation,
            String fromStatus,
            String toStatus,
            Long employeeId,
            String key) {
        one(orderOperationLogMapper.insert(id, operation, fromStatus, toStatus, employeeId, key));
    }

    public int auditCount(Long id, String operation) {
        return orderOperationLogMapper.countByOrderIdAndOperation(id, operation);
    }
}
