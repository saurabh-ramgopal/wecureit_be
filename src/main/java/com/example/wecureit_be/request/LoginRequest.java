package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class LoginRequest {
    String email;
    String password;
    String type;
}
