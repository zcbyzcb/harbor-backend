package com.harbor.hotel.app.inventory.qurier;

import com.harbor.hotel.infrastructure.persistence.mapper.CatalogReadMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListRoomTypeIdsQurier {
    @Resource private CatalogReadMapper mapper;

    public List<Long> query() {
        return mapper.roomTypeIds();
    }
}
