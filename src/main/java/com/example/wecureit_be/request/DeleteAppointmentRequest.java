package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class DeleteAppointmentRequest {
    Integer appointmentId;
    Boolean isActive;
}
