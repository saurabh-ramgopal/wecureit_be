package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class DeleteDoctorRequest {
    Integer doctorMasterId;
    Boolean isActive;
}
