package com.example.wecureit_be.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class PatientAppointments {
    private String doctorName;
    private String speciality;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String facilityName; 
    private String appointmentNotes;
    private Integer appointmentId;
}
