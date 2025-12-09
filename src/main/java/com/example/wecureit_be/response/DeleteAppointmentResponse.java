package com.example.wecureit_be.response;

import lombok.Value;

@Value
public class DeleteAppointmentResponse {
    Integer appointmentId;
    Boolean isActive;
}
