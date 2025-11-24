package com.pulsewatch.common.event;

/**
 * ✅ 점검 요청 이벤트(Producer가 토픽에 발행하는 메시지 형태)
 * - 공통 모듈에 두면 api/worker/notifier 모두 같은 스펙을 공유 가능
 */
public record CheckJobRequested(
        Long targetId,
        String url
) {}
