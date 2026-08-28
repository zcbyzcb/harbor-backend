package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.LockPO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface InventoryLockMapper {
    List<LockPO> findByOrderId(@Param("orderId") Long orderId);
    int insert(@Param("orderId") Long orderId, @Param("inventoryId") Long inventoryId, @Param("count") int count);
    int transition(@Param("id") Long id, @Param("status") String status);
}
