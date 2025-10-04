package me.JAs0n.auth.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAccessSessionStore implements AccessSessionStore {
    private final Map<UUID, String> map = new ConcurrentHashMap<>();
    @Override public void setCurrentJti(UUID userId, String jti) { map.put(userId, jti); }
    @Override public boolean isCurrentJti(UUID userId, String jti) { return jti != null && jti.equals(map.get(userId)); }
    @Override public void revoke(UUID userId) { map.remove(userId); }
}
