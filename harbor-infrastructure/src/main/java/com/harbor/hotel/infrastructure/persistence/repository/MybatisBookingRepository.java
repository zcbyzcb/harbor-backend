package com.harbor.hotel.infrastructure.persistence.repository;

import com.harbor.hotel.domain.booking.repository.BookingRepository;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.infrastructure.persistence.mapper.BookingMapper;
import com.harbor.hotel.infrastructure.persistence.transfer.BookingPersistenceTransfer;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MybatisBookingRepository implements BookingRepository {
    @Resource
    private BookingMapper mapper;

    private void one(int affected) {
        if (affected != 1) throw new DomainException("INVENTORY_STATE_CONFLICT");
    }

    public RoomType lockType(Long id) {
        return BookingPersistenceTransfer.toDomain(mapper.lockType(id));
    }

    public Order findRequest(Long employeeId, String key) {
        return BookingPersistenceTransfer.toDomain(mapper.findRequest(employeeId, key));
    }

    public Long orderType(Long id) {
        return mapper.orderType(id);
    }

    public Order lockOrder(Long id) {
        return BookingPersistenceTransfer.toDomain(mapper.lockOrder(id));
    }

    public Long insertOrder(NewOrder order) {
        com.harbor.hotel.infrastructure.persistence.po.NewOrderPO row =
                BookingPersistenceTransfer.toPO(order);
        one(mapper.insertOrder(row));
        return row.getId();
    }

    public List<Lock> lockReservations(Long id) {
        return mapper.lockReservations(id).stream()
                .map(BookingPersistenceTransfer::toDomain)
                .toList();
    }

    public void insertReservation(Long orderId, Long inventoryId, int count) {
        one(mapper.insertReservation(orderId, inventoryId, count));
    }

    public void updateInventory(Long id, int booked, int checkedIn, int available) {
        one(mapper.updateInventory(id, booked, checkedIn, available));
    }

    public void transitionReservation(Long id, String status) {
        one(mapper.transitionReservation(id, status));
    }

    public List<Room> lockRooms(List<Long> ids) {
        return mapper.lockRooms(ids).stream().map(BookingPersistenceTransfer::toDomain).toList();
    }

    public List<Detail> lockDetails(List<Long> inventories, List<Long> rooms) {
        return mapper.lockDetails(inventories, rooms).stream()
                .map(BookingPersistenceTransfer::toDomain)
                .toList();
    }

    public List<Checkin> checkins(Long id) {
        return mapper.checkins(id).stream().map(BookingPersistenceTransfer::toDomain).toList();
    }

    public Long insertCheckin(NewCheckin checkin) {
        com.harbor.hotel.infrastructure.persistence.po.NewCheckinPO row =
                BookingPersistenceTransfer.toPO(checkin);
        one(mapper.insertCheckin(row));
        return row.getId();
    }

    public void insertGuest(Long id, int seq, Guest guest) {
        one(mapper.insertGuest(id, seq, guest.name(), guest.phone()));
    }

    public void occupyDetail(Long id, Long checkinId) {
        one(mapper.occupyDetail(id, checkinId));
    }

    public void occupyRoom(Long id) {
        one(mapper.occupyRoom(id));
    }

    public void markCheckedIn(Long id) {
        one(mapper.markCheckedIn(id));
    }

    public void markCancelled(Long id, Long employeeId, LocalDateTime now, String reason) {
        one(mapper.markCancelled(id, employeeId, now, reason));
    }

    public void audit(
            Long id,
            String operation,
            String fromStatus,
            String toStatus,
            Long employeeId,
            String key) {
        one(mapper.audit(id, operation, fromStatus, toStatus, employeeId, key));
    }

    public int auditCount(Long id, String operation) {
        return mapper.auditCount(id, operation);
    }
}
