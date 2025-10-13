package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "doctor_master", schema = "public")
public class DoctorMaster {

    @Id
    @Column(name = "doctor_master_id")
    public Integer doctorMasterId;

    @Column(name = "doctor_name")
    public String doctorName;

    @Column(name = "doctor_email")
    public String doctorEmail;

    @Column(name = "doctor_password")
    public String doctorPassword;
}
