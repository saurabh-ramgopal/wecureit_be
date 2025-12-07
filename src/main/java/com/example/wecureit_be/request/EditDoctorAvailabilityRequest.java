package com.example.wecureit_be.request;

import lombok.Data;

import java.time.LocalTime;

@Data
public class EditDoctorAvailabilityRequest {
    String dfAvailabilityId;
    LocalTime availableStartTime;
    LocalTime availableEndTime;
}
