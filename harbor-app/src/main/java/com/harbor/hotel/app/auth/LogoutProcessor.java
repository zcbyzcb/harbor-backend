package com.harbor.hotel.app.auth;

import com.harbor.hotel.domain.auth.SessionEndPort;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class LogoutProcessor {
    @Resource private SessionEndPort sessions;

    public void process() {
        sessions.endCurrentSession();
    }
}
