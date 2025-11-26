package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "patient_master", schema = "public")
public class PatientMaster {

    @Id
    @Column(name = "patient_master_id")
    public Integer patientMasterId;

    @Column(name = "patient_name")
    public String patientName;

    @Column(name = "patient_email")
    public String patientEmail;

    @Column(name = "patient_dob")
    public LocalDate patientDob;

    @Column(name = "patient_gender")
    public String patientGender;

    @Column(name = "patient_phone")
    public String patientPhone;

    @Column(name = "patient_address")
    public String patientAddress;
}
