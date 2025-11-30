package com.example.wecureit_be.response;

import com.example.wecureit_be.entity.DoctorFacilityAvailability;
import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.entity.SpecialityMaster;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookAppointmentResponse {
    public Integer appointmentId;
    public LocalDate date;
    public Integer duration;
    public PatientMaster patientMaster;
    public DoctorFacilityAvailability doctorFacilityAvailability;
    public LocalTime startTime;
    public LocalTime endTime;
    public String appointmentNotes;
    public SpecialityMaster specialityMaster;
    public DoctorMaster doctorMaster;
}
