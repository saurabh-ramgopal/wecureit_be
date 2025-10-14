package com.example.wecureit_be.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {
    String name;
    String email;
    String password;
    LocalDate dob;
}