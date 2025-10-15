package com.example.wecureit_be.response;

import lombok.Value;

@Value
public class LoginResponse {
    String email;
    String type;
    String result;
    String reason;
    String token;
}
