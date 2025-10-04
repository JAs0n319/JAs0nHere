package me.JAs0n.auth.entity;

import jakarta.persistence.*;
import me.JAs0n.auth.convert.UUIDBinaryConverter;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "user")
public class UserEntity {


    @Id
    @Column(name = "uuid", columnDefinition = "BINARY(16)", nullable = false)
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;


    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email; // 小写存储


    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash; // BCrypt


    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 10)
    private Permission permission = Permission.EDITOR;


    // 由数据库默认值维护
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;


    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // NULL = 永久


    public UserEntity() {}


    public UserEntity(UUID uuid, String email, String passwordHash, Permission permission, LocalDateTime expiresAt) {
        this.uuid = uuid;
        this.email = email;
        this.passwordHash = passwordHash;
        this.permission = permission;
        this.expiresAt = expiresAt;
    }


    // getters & setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
