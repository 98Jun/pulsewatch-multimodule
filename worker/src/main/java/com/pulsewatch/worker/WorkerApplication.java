package com.pulsewatch.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ✅ Worker 엔트리 포인트
 * - Kafka 토픽(예: monitor.job.requested) 소비
 * - 점검/비동기 작업 실행 후 결과 발행
 */
@SpringBootApplication(scanBasePackages = "com.pulsewatch")
public class WorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
