package com.example.wecureit_be.response;

import lombok.Data;

import java.util.List;

@Data
public class DoctorStateDetails {
    String stateCode;
    String stateName;
    List<DoctorSpecialityDetails> stateSpecialities;
}