package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class DeleteDoctorAvailabilityRequest {
    String dfAvailabilityId;
    Boolean isActive;
}
