package com.example.wecureit_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "doctor_facility_availability", schema = "public")
public class DoctorFacilityAvailability {

    @Id
    @Column(name = "df_availability_id")
    public String dfAvailabilityId;

    @ManyToOne
    @JoinColumn(name = "doctor_master_id")
    public DoctorMaster doctorMaster;

    @ManyToOne
    @JoinColumn(name = "facility_master_id")
    public FacilityMaster facilityMaster;

    @Column(name = "available_date")
    public LocalDate availableDate;

    @Column(name = "available_start_time")
    public LocalTime availableStartTime;

    @Column(name = "available_end_time")
    public LocalTime availableEndTime;

    @Column(name = "is_active")
    public Boolean isActive;
}
