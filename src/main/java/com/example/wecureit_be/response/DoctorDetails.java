package com.example.wecureit_be.response;

import java.util.List;

import com.example.wecureit_be.entity.SpecialityMaster;

import lombok.Value;

@Value
public class DoctorDetails {
    public Integer doctorMasterId;
    public String doctorName;
    public String doctorEmail;
    public String doctorPassword;
    public String doctorGender;
    public List<SpecialityMaster> speciality;
    public List<DoctorLicense> licenses;
}
