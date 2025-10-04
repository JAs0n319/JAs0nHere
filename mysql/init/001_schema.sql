CREATE TABLE IF NOT EXISTS `user` (
    `uuid`          BINARY(16)   NOT NULL COMMENT '主键 UUID',
    `email`         VARCHAR(255) NOT NULL COMMENT '邮箱',
    `password_hash` VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码哈希',
    `permission`    ENUM('OWNER','ADMIN','EDITOR') NOT NULL DEFAULT 'EDITOR' COMMENT '权限等级',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `expires_at`    DATETIME     NULL DEFAULT NULL COMMENT '失效时间；NULL=永久',
    PRIMARY KEY (`uuid`),
    UNIQUE KEY `uk_user_email` (`email`),
    KEY `idx_user_expires_at` (`expires_at`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 插入时若未提供 uuid，自动设置为“时间友好”的二进制形式
DROP TRIGGER IF EXISTS `user_before_insert_set_uuid`;
DELIMITER //
CREATE TRIGGER `user_before_insert_set_uuid`
    BEFORE INSERT ON `user`
    FOR EACH ROW
BEGIN
    IF NEW.`uuid` IS NULL THEN
    SET NEW.`uuid` = UUID_TO_BIN(UUID(), 1);
END IF;
END//
DELIMITER ;