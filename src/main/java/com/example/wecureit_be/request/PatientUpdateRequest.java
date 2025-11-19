package com.example.wecureit_be.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class PatientUpdateRequest {

    @Email(message = "invalid email format")
    private String email;

    // Allow digits and optional leading +, length between 7 and 15
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "invalid phone number")
    @Size(min = 7, max = 15)
    private String phone;
}
