package me.JAs0n.auth.security;

import java.util.UUID;

public interface AccessSessionStore {
    void setCurrentJti(UUID userId, String jti);
    boolean isCurrentJti(UUID userId, String jti);
    void revoke(UUID userId);
}