package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class CommonLoginRequest {
    String email;
    String password;
    String type;
}
