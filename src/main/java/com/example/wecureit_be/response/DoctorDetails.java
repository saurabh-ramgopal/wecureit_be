package com.example.wecureit_be.response;

import com.example.wecureit_be.entity.SpecialityMaster;
import lombok.Value;

import java.util.List;

@Value
public class DoctorDetails {
    public Integer doctorMasterId;
    public String doctorName;
    public String doctorEmail;
    public String doctorGender;
    public List<SpecialityMaster> speciality;
}
