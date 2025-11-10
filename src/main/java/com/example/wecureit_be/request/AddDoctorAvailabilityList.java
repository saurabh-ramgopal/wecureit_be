package com.example.wecureit_be.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AddDoctorAvailabilityList {
    String facilityId;
    LocalDate availableDate;
    LocalTime availableStartTime;
    LocalTime availableEndTime;
}
