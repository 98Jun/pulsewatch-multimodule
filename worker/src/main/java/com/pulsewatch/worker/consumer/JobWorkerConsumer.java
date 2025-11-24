package com.pulsewatch.worker.consumer;

import com.pulsewatch.common.event.CheckJobRequested;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * ✅ Kafka Consumer 뼈대
 * - 실제 비즈니스 로직/DB 저장은 추후 작성
 */
@Slf4j
@Component
public class JobWorkerConsumer {

    @KafkaListener(topics = "monitor.job.requested", groupId = "pulsewatch-worker")
    public void onJobRequested(CheckJobRequested job) {
        log.info("[WORKER] received job: {}", job);
        // TODO: 점검 로직 수행 -> 결과 토픽 발행 -> DB 저장
    }
}
