package com.harbor.hotel.start.bootstrap;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class JobConfiguration {
    @Bean
    @ConditionalOnProperty(name = "hotel.xxl-job.enabled", havingValue = "true")
    XxlJobSpringExecutor executor(
            @Value("${hotel.xxl-job.admin-addresses}") String admins,
            @Value("${hotel.xxl-job.access-token}") String token,
            @Value("${hotel.xxl-job.app-name:harbor-hotel-executor}") String name,
            @Value("${hotel.xxl-job.port:9999}") int port,
            @Value("${hotel.xxl-job.log-path:./logs/xxl-job}") String logPath) {
        if (admins.isBlank() || token.isBlank())
            throw new IllegalArgumentException("XXL-JOB addresses and access token are required");
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(admins);
        executor.setAccessToken(token);
        executor.setAppname(name);
        executor.setPort(port);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(30);
        return executor;
    }
}
