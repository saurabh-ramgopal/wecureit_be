package com.example.wecureit_be.response;

import lombok.Value;
import java.util.List;

@Value
public class DoctorLicense {
    String stateCode;
    List<String> specialityIds;
}
