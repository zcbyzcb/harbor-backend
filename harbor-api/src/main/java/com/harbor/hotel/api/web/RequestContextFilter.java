package com.harbor.hotel.api.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component("hotelRequestContextFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestContextFilter extends OncePerRequestFilter {
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String id = request.getHeader("X-Request-ID");
        if (id == null || !id.matches("[a-zA-Z0-9-]{8,64}")) id = UUID.randomUUID().toString();
        MDC.put("requestId", id);
        MDC.put("traceId", id);
        response.setHeader("X-Request-ID", id);
        response.setHeader("Cache-Control", "no-store");
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("traceId");
        }
    }
}
