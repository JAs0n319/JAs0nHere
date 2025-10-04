package me.JAs0n.auth.service;

import io.jsonwebtoken.Claims;
import me.JAs0n.auth.entity.UserEntity;
import me.JAs0n.auth.repo.UserRepository;
import me.JAs0n.auth.security.AccessSessionStore;
import me.JAs0n.auth.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class TokenAuthService {

    private final JwtService jwt;
    private final UserRepository users;
    private final AccessSessionStore sessions;

    public TokenAuthService(JwtService jwt, UserRepository users, AccessSessionStore sessions) {
        this.jwt = jwt;
        this.users = users;
        this.sessions = sessions;
    }

    /** 登录后签发 AT，并把“当前 jti”写入会话 */
    public String mintAccessToken(UserEntity u) {
        String at = jwt.generateAccessToken(
                u.getUuid(),
                u.getEmail(),
                u.getPermission().name()// typ=access & jti 由 JwtService 自动加
        );

        System.out.println("!!!!!!!!!!!!!!!!!!!!");
        System.out.println(u.getUuid());
        System.out.println(jwt.claims(at).getId());
        sessions.setCurrentJti(u.getUuid(), jwt.claims(at).getId());
        return at;
    }

    /** 续期：校验旧 AT（未过期、access 类型、当前 jti），然后生成并记录新 AT */
    public String renewAccessToken(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token == null) throw new BadCredentialsException("Missing token");

        Claims c = jwt.claims(token); // 失败会抛异常（过期/签名错误）
        if (!"access".equals(c.get("typ", String.class))) throw new BadCredentialsException("Not access token");

        UUID uid = parseUuid(c.getSubject());
        String jti = c.getId();
        if (!sessions.isCurrentJti(uid, jti)) throw new BadCredentialsException("Stale token");

        UserEntity u = users.findById(uid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String newAt = jwt.generateAccessToken(
                u.getUuid(),
                u.getEmail(),
                u.getPermission().name()
        );
        sessions.setCurrentJti(uid, jwt.claims(newAt).getId());
        return newAt;
    }

    /** Filter 用：从 Authorization 构建 Authentication（失败返回 null，让 Security 决定 401） */
    public UsernamePasswordAuthenticationToken authenticateFromHeader(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token == null) return null;

        try {
            Claims c = jwt.claims(token);                      // 验签+未过期
            if (!"access".equals(c.get("typ", String.class))) return null;

            UUID uid = parseUuid(c.getSubject());
            String jti = c.getId();
            if (!sessions.isCurrentJti(uid, jti)) return null; // 旧票/被窃副本

            UserEntity u = users.findById(uid).orElse(null);
            if (u == null) return null;

            Set<SimpleGrantedAuthority> auths =
                    Set.of(new SimpleGrantedAuthority("ROLE_" + u.getPermission().name()));
            return new UsernamePasswordAuthenticationToken(u.getEmail(), null, auths);
        } catch (Exception ignore) {
            return null;
        }
    }

    public void revokeForUser(UUID userId) { sessions.revoke(userId); }

    private static UUID parseUuid(String s) {
        try { return UUID.fromString(s); } catch (Exception e) { throw new BadCredentialsException("Bad subject"); }
    }

    private static String extractToken(String header) {
        if (header == null) return null;
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}
