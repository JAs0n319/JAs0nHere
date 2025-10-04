package me.JAs0n.auth.repo;

import me.JAs0n.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String emailLower);
    boolean existsByEmail(String emailLower);
}
