package com.harbor.hotel.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Param;

public interface OrderOperationLogMapper {
    int insert(@Param("orderId") Long orderId, @Param("operation") String operation,
            @Param("fromStatus") String fromStatus, @Param("toStatus") String toStatus,
            @Param("employeeId") Long employeeId, @Param("requestId") String requestId);
    int countByOrderIdAndOperation(@Param("orderId") Long orderId, @Param("operation") String operation);
}
