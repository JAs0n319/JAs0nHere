package me.JAs0n.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private static final String CLAIM_TYP   = "typ";     // 令牌类型
    private static final String TYP_ACCESS  = "access";  // 仅做 AT
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE  = "role";

    private final Key key;

    @Getter
    private final long expiresMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String base64Secret,
            @Value("${app.security.jwt.expires-minutes}") long expiresMinutes) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.expiresMinutes = expiresMinutes;
    }

    /**
     * 生成「Access Token」
     */
    public String generateAccessToken(UUID userId, String email, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresMinutes * 60);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TYP, TYP_ACCESS);
        if (email != null) claims.put(CLAIM_EMAIL, email);
        if (role  != null) claims.put(CLAIM_ROLE,  role);

        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())     // jti
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /** 校验签名与过期（抛异常视为无效）。 */
    public Claims claims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** token 是否有效（签名/未过期）。 */
    public boolean isValid(String token) {
        try { claims(token); return true; } catch (Exception e) { return false; }
    }

    /** subject（通常为 userId）。 */
    public String subject(String token) { return claims(token).getSubject(); }

    /** jti（用于服务端会话校验/轮换）。 */
    public String jti(String token) { return claims(token).getId(); }

    /** 是否 access token。 */
    public boolean isAccessToken(String token) {
        return TYP_ACCESS.equals(claims(token).get(CLAIM_TYP, String.class));
    }

    /** 过期时间（UTC）。 */
    public Instant expiresAt(String token) {
        return claims(token).getExpiration().toInstant();
    }
}
