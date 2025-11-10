package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "doctor_master_id")
    public Integer doctorMasterId;

    @Column(name = "facility_master_id")
    public String facilityMasterId;

    @Column(name = "available_date")
    public LocalDate availableDate;

    @Column(name = "available_start_time")
    public LocalTime availableStartTime;

    @Column(name = "available_end_time")
    public LocalTime availableEndTime;
}
