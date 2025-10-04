package me.JAs0n.auth.service;

import me.JAs0n.auth.dto.AuthResponse;
import me.JAs0n.auth.dto.LoginRequest;
import me.JAs0n.auth.dto.RegisterRequest;
import me.JAs0n.auth.entity.UserEntity;
import me.JAs0n.auth.repo.UserRepository;
import me.JAs0n.auth.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;


@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenAuthService tokens;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, TokenAuthService tokens,  JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.tokens = tokens;
        this.jwt = jwt;
    }


    public void register(RegisterRequest req) {
        String email = normalizeEmail(req.getEmail());
        if (users.existsByEmail(email)) {
            throw new IllegalStateException("EMAIL_EXISTS");
        }
        if (req.getPassword().length() < 8) {
            throw new IllegalStateException("PASSWORD_TOO_SHORT");
        }
        if (req.getPermission() == null) {
            throw new IllegalStateException("PERMISSION_NULL");
        }
        UUID uuid = UUID.randomUUID();
        String hash = encoder.encode(req.getPassword());
        UserEntity u = new UserEntity(uuid, email, hash, req.getPermission(), null);
        users.save(u);
    }


    public AuthResponse login(LoginRequest req) {
        UserEntity u = users.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BadCredentialsException("Bad credentials");
        }

        String at = tokens.mintAccessToken(u); // 这里会写入“当前 jti”
        long expiresInSeconds = Math.max(0,
                Duration.between(Instant.now(), jwt.expiresAt(at)).getSeconds());

        return new AuthResponse(at, expiresInSeconds);
    }

    public AuthResponse renew(String authorizationHeader) {
        String newAt = tokens.renewAccessToken(authorizationHeader);
        long expiresInSeconds = Math.max(0,
                Duration.between(Instant.now(), jwt.expiresAt(newAt)).getSeconds());
        return new AuthResponse(newAt, expiresInSeconds);
    }


    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
