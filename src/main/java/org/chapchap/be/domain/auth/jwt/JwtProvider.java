package org.chapchap.be.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private SecretKey key;
    private JwtParser jwtParser;

    @Value("${app.auth.jwt.secret}")
    private String secret;

    @Value("${app.auth.jwt.access-exp-sec}")
    private long accessExpSec;

    @Value("${app.auth.jwt.refresh-exp-sec:1209600}")
    private long refreshExpSec;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.jwtParser = Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(60) // 만료 오차 - 60초 허용
                .build();
    }

    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(accessExpSec)))
                .claim("typ", "access")
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(refreshExpSec)))
                .claim("typ", "refresh")
                .signWith(key)
                .compact();
    }

    public Long parseUserId(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public boolean isExpired(String token) {
        try {
            Date exp = jwtParser.parseSignedClaims(token).getPayload().getExpiration();
            return exp.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}