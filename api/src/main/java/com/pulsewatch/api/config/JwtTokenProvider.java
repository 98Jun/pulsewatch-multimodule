package com.pulsewatch.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * packageName    : com.pulsewatch.api.config
 * fileName       : JwtTokenProvider
 * author         : jun
 * date           : 25. 11. 25.
 * description    : JWT 토큰을 생성하고, 검증하고, 토큰에서 클레임(사용자 정보/권한 등)을 꺼내는 역할을 담당하는 공통 유틸 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 11. 25.        jun       최초 생성
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessExpireMs;

    /**
     * 생성자
     * - application.yml 에 설정된 jwt.secret, jwt.access-token.expire-time 값을 주입받는다.
     * - secret 문자열을 이용해 HMAC-SHA 알고리즘에 사용할 SecretKey 객체를 생성한다.
     * - accessExpireMs 는 Access Token 이 얼마나 오래 유효한지(밀리초 기준) 저장한다.
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expire-time}") long accessExpireMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpireMs = accessExpireMs;
    }

    /**
     * Access Token 생성 메서드
     *
     * @param subject 토큰의 주체(subject). 보통 로그인한 사용자의 ID 또는 이메일 등을 넣는다.
     * @param roles   사용자의 권한 목록. 예) ["ROLE_USER", "ROLE_ADMIN"]
     * @return 서명(sign)된 JWT 문자열 (클라이언트에게 발급해서 Authorization 헤더에 실어서 쓰는 값)
     *
     * 내부 동작
     * - 현재 시간(now) 기준으로 만료 시간(exp)을 accessExpireMs 만큼 더해서 계산한다.
     * - subject, 발급 시각(issuedAt), 만료 시각(expiration), roles(claim)을 JWT payload 에 넣는다.
     * - secretKey 로 HS256 알고리즘을 사용해 토큰에 서명한 뒤 최종 문자열을 반환한다.
     */
    public String createAccessToken(String subject, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpireMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("roles", roles)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증 메서드
     *
     * @param token 클라이언트가 보낸 JWT 문자열 ("Bearer "를 제외한 순수 토큰 값)
     * @return 토큰이 형식적으로 올바르고, 서명이 정상이며, 만료되지 않았다면 true, 그 외에는 false
     *
     * 내부 동작
     * - JJWT 의 parser 를 사용해 현재 SecretKey 로 서명을 검증한다.
     * - 토큰 구조가 깨졌거나, 서명이 위조되었거나, 만료(expired)된 경우 JwtException / IllegalArgumentException 이 발생한다.
     * - 예외가 발생하지 않으면 유효한 토큰으로 판단하고 true 를 반환한다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWT 로부터 클레임(Claims) 객체를 추출하는 메서드
     *
     * @param token 서명된 JWT 문자열
     * @return JWT 의 payload 부분을 파싱한 Claims 객체
     *
     * 내부 동작
     * - validateToken 과 동일하게 SecretKey 로 서명을 검증한 뒤,
     *   토큰의 payload(= 클레임 집합)를 Claims 형태로 꺼낸다.
     * - 이후 getSubject(), getRoles() 등의 메서드에서 이 Claims 를 이용해 개별 필드를 읽는다.
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰에서 subject(주체, 보통 사용자 ID/이메일)를 꺼내는 헬퍼 메서드
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰에서 roles 클레임(사용자의 권한 목록)을 꺼내는 헬퍼 메서드
     *
     * @return 예) ["ROLE_USER", "ROLE_ADMIN"] 형태의 권한 문자열 리스트
     */
    public List<String> getRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }
}
