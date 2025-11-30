package com.example.wecureit_be.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookAppointmentRequest {
    public LocalDate date;
    public Integer duration;
    public Integer patientMasterId;
    public String dfAvailabilityId;
    public LocalTime startTime;
    public LocalTime endTime;
    public String specialityMasterId;
    public Integer doctorMasterId;
}
