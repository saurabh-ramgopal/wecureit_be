package com.example.wecureit_be.response;

import lombok.Value;

import java.util.List;

@Value
public class DoctorDetails {
    public Integer doctorMasterId;
    public String doctorName;
    public String doctorEmail;
    public String doctorGender;
    public Boolean isActive;
    public Boolean isDeletable;
    public List<DoctorStateDetails> stateDetails;
}
