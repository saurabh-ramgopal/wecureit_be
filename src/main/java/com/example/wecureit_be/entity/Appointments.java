package com.example.wecureit_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "appointments", schema = "public")
public class Appointments {

    @Id
    @Column(name = "appointment_id")
    public Integer appointmentId;

    @Column(name = "date")
    public LocalDate date;

    @Column(name = "duration")
    public Integer duration;

    @ManyToOne
    @JoinColumn(name = "patient_master_id")
    public PatientMaster patientMaster;

    @ManyToOne
    @JoinColumn(name = "df_availability_id")
    public DoctorFacilityAvailability doctorFacilityAvailability;

    @Column(name = "start_time")
    public LocalTime startTime;

    @Column(name = "end_time")
    public LocalTime endTime;

    @Column(name = "appointment_notes")
    public String appointmentNotes;

    @ManyToOne
    @JoinColumn(name = "speciality_master_id")
    public SpecialityMaster specialityMaster;

    @ManyToOne
    @JoinColumn(name = "doctor_master_id")
    public DoctorMaster doctorMaster;


}
