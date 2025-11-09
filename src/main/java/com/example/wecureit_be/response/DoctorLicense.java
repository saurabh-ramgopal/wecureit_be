package com.example.wecureit_be.response;

import java.util.List;

import lombok.Value;

@Value
public class DoctorLicense {
    String stateCode;
    List<String> specialityIds;
}
