package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.RoomTypePO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RoomTypeMapper {
    List<RoomTypePO> findActive();
    RoomTypePO findById(@Param("id") Long id);
}
