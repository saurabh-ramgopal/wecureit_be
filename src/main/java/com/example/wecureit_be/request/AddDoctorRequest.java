package com.example.wecureit_be.request;

import lombok.Data;

import java.util.List;

@Data
public class AddDoctorRequest {
    String doctorName;
    String doctorGender;
    String doctorEmail;
    String doctorPassword;
    List<String> specialityList;
}