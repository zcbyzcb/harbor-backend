package com.harbor.hotel.api.security;

import com.harbor.hotel.domain.auth.SessionEndPort;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionEndAdapter implements SessionEndPort {
    public void endCurrentSession() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler()
                .logout(attributes.getRequest(), attributes.getResponse(), authentication);
        new CookieClearingLogoutHandler("JSESSIONID")
                .logout(attributes.getRequest(), attributes.getResponse(), authentication);
    }
}
