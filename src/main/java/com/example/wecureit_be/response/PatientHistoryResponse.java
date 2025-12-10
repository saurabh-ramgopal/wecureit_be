package com.example.wecureit_be.response;

import lombok.Value;
import java.util.List;

@Value
public class PatientHistoryResponse {
    String patientName;
    String patientAge;
    String patientGender;
    List<PatientHistoryResponseList> history;
}