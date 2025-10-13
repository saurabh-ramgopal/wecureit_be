package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "patient_master", schema = "public")
public class PatientMaster {

    @Id
    @Column(name = "patient_master_id")
    public String patientMasterId;

    @Column(name = "patient_name")
    public String patientName;

    @Column(name = "patient_password")
    public String patientPassword;

    @Column(name = "patient_email")
    public String patientEmail;
}
