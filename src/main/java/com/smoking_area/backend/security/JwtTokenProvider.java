package com.smoking_area.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // 파라미터 이름을 AuthService에서 넘겨주는 DB 고유 id 의미에 맞춰 userId로 명확하게 수정했습니다.
    public String createToken(Long userId, String nickname) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("nickname", nickname)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- 새로 추가된 기능: 토큰을 복호화해서 유저 인증 정보(Authentication) 획득 ---
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 토큰 Subject에 담아둔 유저 PK(id)를 가져옵니다.
        String userId = claims.getSubject();

        // Spring Security가 인식할 수 있는 인증 객체 반환 (기본 권한 ROLE_USER 부여)
        return new UsernamePasswordAuthenticationToken(userId, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // --- 새로 추가된 기능: 토큰의 위변조 및 만료 여부 검증 ---
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 기한 만료, 잘못된 서명 등의 에러가 발생하면 false 반환
            return false;
        }
    }
}