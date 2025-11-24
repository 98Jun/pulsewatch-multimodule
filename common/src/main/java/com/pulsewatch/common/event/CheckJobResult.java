package com.pulsewatch.common.event;

/**
 * ✅ 점검 결과 이벤트(Worker가 발행)
 */
public record CheckJobResult(
        Long targetId,
        boolean up,
        long latencyMs
) {}
