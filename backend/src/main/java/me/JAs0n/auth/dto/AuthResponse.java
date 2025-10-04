package me.JAs0n.auth.dto;

public class AuthResponse {
    final String tokenType = "Bearer";
    final String accessToken;
    final long expiresInSeconds;


    public AuthResponse(String accessToken, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
    }


    public String getTokenType() { return tokenType; }
    public String getAccessToken() { return accessToken; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
}
