package com.example.wecureit_be.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientHistoryResponseList {
    private String facilityName;
    private String specialityName;
    private String appointmentNote;
    private String doctorName;
    private LocalDate appointmentDate;
}