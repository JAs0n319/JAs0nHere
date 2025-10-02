package me.JAs0n;

import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
public class TestController {

    // GET /api/ping  -> {"ok":true,"msg":"pong","ts":"..."}
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "ok", true,
                "msg", "pong",
                "ts", Instant.now().toString()
        );
    }

    // POST /api/echo  (body: {"hello":"world"}) -> 原样回显并带时间戳
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
        return Map.of(
                "ok", true,
                "echo", body,
                "ts", Instant.now().toString()
        );
    }
}
