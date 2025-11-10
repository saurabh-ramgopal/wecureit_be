package com.example.wecureit_be.request;
import lombok.Data;

import java.util.List;

@Data
public class AddDoctorAvailabilityRequest {
    Integer doctorId;
    List<AddDoctorAvailabilityList> facilityList;
}