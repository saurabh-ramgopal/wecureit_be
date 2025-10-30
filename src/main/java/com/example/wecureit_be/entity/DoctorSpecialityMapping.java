package com.example.wecureit_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "doctor_speciality_mapping", schema = "public")
@IdClass(DoctorSpecialityMappingId.class)
public class DoctorSpecialityMapping implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "doctor_master_id", referencedColumnName = "doctor_master_id")
    private DoctorMaster doctorMaster;

    @Id
    @ManyToOne
    @JoinColumn(name = "speciality_master_id", referencedColumnName = "speciality_master_id")
    private SpecialityMaster specialityMaster;

}
