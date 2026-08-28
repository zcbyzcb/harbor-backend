package com.harbor.hotel.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "com.harbor.hotel",
        exclude =
                org.springframework.boot.autoconfigure.security.servlet
                        .UserDetailsServiceAutoConfiguration.class)
@MapperScan("com.harbor.hotel.infrastructure.persistence.mapper")
public class HarborHotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(HarborHotelApplication.class, args);
    }
}
