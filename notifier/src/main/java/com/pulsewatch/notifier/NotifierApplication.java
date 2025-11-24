package com.pulsewatch.notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ✅ Notifier 엔트리 포인트
 * - 결과/알림 이벤트를 Kafka에서 소비
 * - 텔레그램/메일/웹훅 등으로 알림 발송(추후 구현)
 */
@SpringBootApplication(scanBasePackages = "com.pulsewatch")
public class NotifierApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotifierApplication.class, args);
    }
}
