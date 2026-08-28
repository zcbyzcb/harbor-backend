package com.harbor.hotel.infrastructure.persistence.po;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class NewOrderPO {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private final String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    private final Long employeeId;

    public Long getEmployeeId() {
        return employeeId;
    }

    private final String requestId;

    public String getRequestId() {
        return requestId;
    }

    private final byte[] requestHash;

    public byte[] getRequestHash() {
        return requestHash;
    }

    private final Long roomTypeId;

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    private final String roomTypeName;

    public String getRoomTypeName() {
        return roomTypeName;
    }

    private final int roomCount;

    public int getRoomCount() {
        return roomCount;
    }

    private final String bookerName;

    public String getBookerName() {
        return bookerName;
    }

    private final String bookerPhone;

    public String getBookerPhone() {
        return bookerPhone;
    }

    private final LocalDateTime checkinTime;

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    private final LocalDateTime checkoutTime;

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    private final int nights;

    public int getNights() {
        return nights;
    }

    private final BigDecimal nightlyPrice;

    public BigDecimal getNightlyPrice() {
        return nightlyPrice;
    }

    private final BigDecimal totalAmount;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    private final String remark;

    public String getRemark() {
        return remark;
    }

    public NewOrderPO(
            String orderNo,
            Long employeeId,
            String requestId,
            byte[] requestHash,
            Long roomTypeId,
            String roomTypeName,
            int roomCount,
            String bookerName,
            String bookerPhone,
            LocalDateTime checkinTime,
            LocalDateTime checkoutTime,
            int nights,
            BigDecimal nightlyPrice,
            BigDecimal totalAmount,
            String remark) {
        this.orderNo = orderNo;
        this.employeeId = employeeId;
        this.requestId = requestId;
        this.requestHash = requestHash;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.roomCount = roomCount;
        this.bookerName = bookerName;
        this.bookerPhone = bookerPhone;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
        this.nights = nights;
        this.nightlyPrice = nightlyPrice;
        this.totalAmount = totalAmount;
        this.remark = remark;
    }
}
