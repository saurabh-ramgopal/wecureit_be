package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class PatientBookingRequest {
    Integer doctorMasterId;
    String specialityMasterId;
    String facilityMasterId;
}