package com.example.wecureit_be.response;

import lombok.Value;

@Value
public class DeleteDoctorAvailabilityResponse {
    String dfAvailabilityId;
    Boolean isActive;
}
