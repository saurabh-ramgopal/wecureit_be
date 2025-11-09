package com.example.wecureit_be.request;

import java.util.List;

import lombok.Data;

@Data
public class AddDoctorRequest {
    String doctorName;
    String doctorGender;
    String doctorEmail;
    String doctorPassword;
    Integer doctorMasterId;
    List<String> specialityList;
    List<com.example.wecureit_be.request.DoctorStateSpeciality> doctorStateSpeciality;
}