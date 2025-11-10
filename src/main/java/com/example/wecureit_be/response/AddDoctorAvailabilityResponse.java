package com.example.wecureit_be.response;

import com.example.wecureit_be.request.AddDoctorAvailabilityList;
import lombok.Value;

import java.util.List;

@Value
public class AddDoctorAvailabilityResponse {
    Integer doctorId;
    List<AddDoctorAvailabilityList> facilityList;
}