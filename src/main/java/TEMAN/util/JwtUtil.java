package TEMAN.util;

import TEMAN.domain.enums.RoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // application.yml에 적어둔 비밀키와 만료시간을 가져옵니다.
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    // application.yml의 비밀키를 암호화 알고리즘에 맞는 Key 객체로 변환
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 🚀 진짜 Access Token 생성 메서드
    public String createAccessToken(String email, RoleEnum role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + (accessTokenValidityInSeconds * 1000));

        return Jwts.builder()
                .setSubject(email) // 토큰의 주인 (이메일)
                .claim("role", role.name()) // 권한 정보 추가
                .setIssuedAt(now) // 발급 시간
                .setExpiration(validity) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // HS256 알고리즘으로 암호화 서명!
                .compact(); // 토큰 문자열로 압축
    }
}