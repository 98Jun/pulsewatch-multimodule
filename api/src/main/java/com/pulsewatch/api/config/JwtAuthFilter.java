package com.pulsewatch.api.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : com.pulsewatch.api.config
 * fileName       : JwtAuthFilter
 * author         : jun
 * date           : 25. 11. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 11. 28.        jun       최초 생성
 */
/**
 *  JWT 인증 필터
 * - HTTP 요청 헤더에서 JWT 토큰을 꺼내고
 * - 유효하면 SecurityContext 에 인증 정보를 넣어주는 역할
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // 🔹 토큰 검사에서 제외할 경로들 (회원가입, 로그인, 스웨거 같은 것들)
    private static final List<String> EXCLUDE_PATHS = List.of(
            "/auth/join",
            "/auth/login",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    /**
     * 이 메서드가 true 를 리턴하면
     * → 아래 doFilterInternal 은 아예 실행 안 됨 (즉, 필터 스킵)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // /auth/join, /auth/login, /swagger-ui/... 이런 경로는 필터 태우지 않기
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 요청마다 딱 한 번 실행되는 부분
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 꺼내기
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // "Authorization: Bearer xxx..." 형식이 아니면 그냥 넘김 (인증 없는 요청)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 이후 순수 토큰만

        try {
            // 2. 토큰 유효성 검증
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("[JWT] 유효하지 않은 토큰입니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 3. 토큰에서 사용자 정보(subject, roles) 꺼내기
            Claims claims = jwtTokenProvider.getClaims(token);
            String subject = claims.getSubject();          // 보통 사용자 ID
            List<String> roles = jwtTokenProvider.getRoles(token);

            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 4. 스프링 시큐리티가 이해할 수 있는 Authentication 객체 만들기
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, authorities);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 5. SecurityContext 에 현재 사용자 정보 넣기
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            log.warn("[JWT] 토큰 처리 중 예외 발생: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 6. 나머지 필터 / 컨트롤러로 진행
        filterChain.doFilter(request, response);
    }
}
