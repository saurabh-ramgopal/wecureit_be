package com.example.wecureit_be.response;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.FacilityMaster;
import com.example.wecureit_be.entity.SpecialityMaster;
import lombok.Value;

import java.util.List;

@Value
public class PatientBookingL1Response {
    List<FacilityMaster> facilityMasterList;
    List<SpecialityMaster> specialityMasterList;
    List<DoctorMaster> doctorMasterList;
}