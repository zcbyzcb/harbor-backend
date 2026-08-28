package com.harbor.hotel.api.web.auth.controller;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.api.web.auth.request.LoginRequest;
import com.harbor.hotel.api.web.auth.transfer.AuthTransfer;
import com.harbor.hotel.api.web.auth.vo.CsrfVO;
import com.harbor.hotel.api.web.auth.vo.EmployeeVO;
import com.harbor.hotel.app.auth.dto.EmployeeDTO;
import com.harbor.hotel.app.auth.dto.LoginDTO;
import com.harbor.hotel.app.auth.processor.LoginProcessor;
import com.harbor.hotel.app.auth.processor.LogoutProcessor;
import com.harbor.hotel.app.auth.qurier.GetCurrentEmployeeQurier;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Resource private LogoutProcessor logoutProcessor;
    @Resource private LoginProcessor loginProcessor;
    @Resource private GetCurrentEmployeeQurier currentEmployeeQurier;
    @Resource private SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy =
            new ChangeSessionIdAuthenticationStrategy();

    @GetMapping("/csrf")
    public ResponseEntity<ApiResponse<CsrfVO>> csrf(CsrfToken token) {
        return noStore(ApiResponse.success(new CsrfVO(token.getHeaderName(), token.getToken())));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<EmployeeVO>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        EmployeeDTO result =
                loginProcessor.process(
                        new LoginDTO(
                                request.username(),
                                request.password(),
                                servletRequest.getRemoteAddr()));
        Authentication authenticated =
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken
                        .authenticated(AuthTransfer.toIdentity(result), null, java.util.List.of());
        sessionAuthenticationStrategy.onAuthentication(
                authenticated, servletRequest, servletResponse);
        new org.springframework.security.web.csrf.CsrfAuthenticationStrategy(
                        new org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository())
                .onAuthentication(authenticated, servletRequest, servletResponse);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return noStore(ApiResponse.success(AuthTransfer.toVO(result)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<EmployeeVO>> me(
            @AuthenticationPrincipal EmployeeSessionIdentity identity) {
        return noStore(
                ApiResponse.success(AuthTransfer.toVO(currentEmployeeQurier.query(identity))));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        logoutProcessor.process();
        return noStore(ApiResponse.success(null));
    }

    private <T> ResponseEntity<ApiResponse<T>> noStore(ApiResponse<T> body) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(body);
    }
}
