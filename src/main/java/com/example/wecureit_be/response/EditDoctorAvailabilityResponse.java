package com.example.wecureit_be.response;

import lombok.Value;

import java.time.LocalTime;

@Value
public class EditDoctorAvailabilityResponse {
    String dfAvailabilityId;
    LocalTime availableStartTime;
    LocalTime availableEndTime;
}
