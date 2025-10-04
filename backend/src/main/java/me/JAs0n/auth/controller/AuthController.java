package me.JAs0n.auth.controller;

import me.JAs0n.auth.dto.AuthResponse;
import me.JAs0n.auth.dto.LoginRequest;
import me.JAs0n.auth.dto.RegisterRequest;
import me.JAs0n.auth.entity.UserEntity;
import me.JAs0n.auth.repo.UserRepository;
import me.JAs0n.auth.security.JwtService;
import me.JAs0n.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth") // 注意：全局 context-path=/api，这里不要再写 /api 前缀
public class AuthController {

    private final JwtService jwt;                // 你还需要它做别的可以保留
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(JwtService jwt, AuthService authService, UserRepository userRepository) {
        this.jwt = jwt;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/renew")
    public ResponseEntity<AuthResponse> renew(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            return ResponseEntity.ok(authService.renew(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 受保护示例：返回当前用户基本信息
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        String email = authentication.getName();
        UserEntity u = userRepository.findByEmail(email).orElseThrow();
        return Map.of(
                "uuid", u.getUuid().toString(),
                "email", u.getEmail(),
                "permission", u.getPermission().name()
        );
    }
}
