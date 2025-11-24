package com.pulsewatch.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ✅ API 서버 엔트리 포인트
 * - 외부 요청(REST) 수신 + Swagger 제공
 * - 비즈니스 로직은 service/mapper로 확장
 */
@SpringBootApplication(scanBasePackages = "com.pulsewatch")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
