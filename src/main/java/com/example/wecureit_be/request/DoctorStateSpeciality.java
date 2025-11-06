package com.example.wecureit_be.request;

import lombok.Data;

import java.util.List;

@Data
public class DoctorStateSpeciality {
    String stateCode;
    List<String> specialityList;
}