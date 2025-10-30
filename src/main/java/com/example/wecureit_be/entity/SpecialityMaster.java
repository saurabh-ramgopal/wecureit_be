package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "speciality_master", schema = "public")
public class SpecialityMaster {

    @Id
    @Column(name = "speciality_master_id")
    public String specialityMasterId;

    @Column(name = "speciality_name")
    public String specialityName;
}
