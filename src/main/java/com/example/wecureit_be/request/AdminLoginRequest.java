package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class AdminLoginRequest {
    String email;
    String password;
}
