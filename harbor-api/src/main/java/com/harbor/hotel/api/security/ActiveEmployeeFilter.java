package com.harbor.hotel.api.security;

import com.harbor.hotel.domain.auth.EmployeeActivityPort;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ActiveEmployeeFilter extends OncePerRequestFilter {
    @Resource private EmployeeActivityPort employeeActivityPort;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.getPrincipal() instanceof EmployeeSessionIdentity employee
                && !employeeActivityPort.isActive(employee.employeeId())) {
            SecurityContextHolder.clearContext();
            var session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
        filterChain.doFilter(request, response);
    }
}
