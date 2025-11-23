package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    String email;
    String name;
    String firebaseUid;
}
