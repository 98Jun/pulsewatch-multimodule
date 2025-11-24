package com.pulsewatch.notifier.consumer;

import com.pulsewatch.common.event.CheckJobResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * ✅ Notifier Kafka Consumer 뼈대
 */
@Slf4j
@Component
public class AlertConsumer {

    @KafkaListener(topics = "monitor.job.result", groupId = "pulsewatch-notifier")
    public void onResult(CheckJobResult result) {
        log.info("[NOTIFIER] received result: {}", result);
        // TODO: 알림 조건 평가 -> 발송 -> DB 저장
    }
}
