package com.harbor.hotel.api.security;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class HotelSecurityConfiguration {

    @Bean
    org.springframework.boot.web.servlet.FilterRegistrationBean<ActiveEmployeeFilter>
            disableDuplicateFilter(ActiveEmployeeFilter filter) {
        org.springframework.boot.web.servlet.FilterRegistrationBean<ActiveEmployeeFilter> registration =
                new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, ActiveEmployeeFilter activeEmployeeFilter) throws Exception {
        http.csrf(
                        csrf ->
                                csrf.csrfTokenRepository(
                                        new org.springframework.security.web.csrf
                                                .HttpSessionCsrfTokenRepository()))
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                        .sessionFixation(fixation -> fixation.changeSessionId()))
                .authorizeHttpRequests(
                        registry ->
                                registry.requestMatchers(
                                                "/api/auth/csrf",
                                                "/api/auth/login",
                                                "/api/auth/logout")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        errors ->
                                errors.authenticationEntryPoint(
                                                (request, response, exception) ->
                                                        writeError(
                                                                response, 401, "UNAUTHENTICATED"))
                                        .accessDeniedHandler(
                                                (request, response, exception) ->
                                                        writeError(
                                                                response,
                                                                403,
                                                                exception
                                                                                instanceof
                                                                                org.springframework
                                                                                        .security
                                                                                        .web.csrf
                                                                                        .CsrfException
                                                                        ? "CSRF_INVALID"
                                                                        : "FORBIDDEN")))
                .addFilterAfter(activeEmployeeFilter, AnonymousAuthenticationFilter.class);
        return http.build();
    }

    private void writeError(HttpServletResponse response, int status, String code)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter()
                .write("{\"code\":\"" + code + "\",\"message\":\"认证状态无效\",\"data\":null}");
    }
}
