package com.harbor.hotel.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Param;

public interface EmployeeAuthMapper {
    EmployeeAuthPO findActiveByUsername(@Param("username") String username);

    EmployeeAuthPO findActiveById(@Param("id") Long id);

    record EmployeeAuthPO(Long id, String username, String displayName, String passwordHash) {}
}
