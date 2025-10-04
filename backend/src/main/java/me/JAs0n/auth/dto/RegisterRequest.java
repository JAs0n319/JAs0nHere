package me.JAs0n.auth.dto;

import lombok.Setter;
import lombok.Getter;
import me.JAs0n.auth.entity.Permission;

@Setter
@Getter
public class RegisterRequest {
    private String email;
    private String password;
    private Permission permission;
}
