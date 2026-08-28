package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.CheckinPO;
import com.harbor.hotel.infrastructure.persistence.po.NewCheckinPO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CheckinMapper {
    List<CheckinPO> findByOrderId(@Param("orderId") Long orderId);
    int insert(NewCheckinPO row);
    int insertGuest(@Param("checkinId") Long checkinId, @Param("seq") int seq,
            @Param("name") String name, @Param("phone") String phone,
            @Param("identityNo") String identityNo);
}
