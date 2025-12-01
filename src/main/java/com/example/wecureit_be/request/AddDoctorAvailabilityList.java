package com.example.wecureit_be.request;

import com.example.wecureit_be.entity.PractisingSpeciality;
import com.example.wecureit_be.entity.SpecialityMaster;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AddDoctorAvailabilityList {
    String facilityId;
    String facilityName;
    LocalDate availableDate;
    LocalTime availableStartTime;
    LocalTime availableEndTime;
    List<SpecialityMaster> speciality;
    String stateName;
    String stateCode;
}
