package com.pulsewatch.api.config;

import com.pulsewatch.api.common.domain.LogVO;
import com.pulsewatch.api.common.service.CommonService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import org.springframework.util.AntPathMatcher;

import java.util.List;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // ✅ 필터 체인 가장 앞쪽에서 동작(시큐리티로 막힌 것도 로그 가능)
public class ApiLoggingFilter extends OncePerRequestFilter {
    @Autowired
    private CommonService commonService;

    private static final int MAX_BODY_LENGTH = 2000; // ✅ 바디 로그 너무 길면 잘라서 출력

    // ✅ 로깅 제외할 URL 패턴(ant-style)
    // 필요하면 여기 패턴만 추가하면 됨.
    private static final List<String> EXCLUDE_PATTERNS = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/favicon.ico",
            "/error",
            "/static/**",
            "/webjars/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * ✅ 특정 URL은 로깅/DB저장을 아예 건너뛰기
     * - OncePerRequestFilter가 true면 doFilterInternal 자체를 호출하지 않음
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDE_PATTERNS.stream().anyMatch(p -> pathMatcher.match(p, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        //request/response 바디를 읽어도 실제 처리에 영향 없게 “캐싱 래퍼”로 감싼다
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(req, res); //실제 컨트롤러/시큐리티 타는 구간
        } finally {

            int status = res.getStatus();
            String method = req.getMethod();
            String uri = req.getRequestURI();
            String query = req.getQueryString();

            // 요청/응답 바디는 텍스트/JSON 계열만 조건부로 로그
            String reqBody = getBody(req.getContentAsByteArray(), req.getContentType());
            String resBody = getBody(res.getContentAsByteArray(), res.getContentType());

            //응답 받은 로그 기록 -> db 로 변경할 예정
            log.info("[API] {} {}{} status={}  reqBody={} resBody={}",
                    method,
                    uri,
                    (query != null ? "?" + query : ""),
                    status,
                    reqBody,
                    resBody
            );

            commonService.setInsertLog(new LogVO(method,uri,query,status,reqBody,resBody));
            res.copyBodyToResponse(); //캐싱 래퍼 썼으면 마지막에 반드시 바디를 다시 써야함
        }
    }

    private String getBody(byte[] bytes, String contentType) {
        if (bytes == null || bytes.length == 0) return "-";

        // 부하를 줄이기 위해.. 바이너리/파일/스트리밍은 바디 찍지 않음
        if (contentType == null) return "-";
        String ct = contentType.toLowerCase();
        if (!(ct.contains("application/json") || ct.contains("text") || ct.contains("xml"))) {
            return "-";
        }

        String body = new String(bytes, StandardCharsets.UTF_8);
        body = body.replaceAll("\\s+", " ").trim(); // 줄바꿈/공백 정리

        if (body.length() > MAX_BODY_LENGTH) {
            return body.substring(0, MAX_BODY_LENGTH) + "...(truncated)";
        }
        return body;
    }
}