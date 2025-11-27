package com.example.wecureit_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "practising_speciality", schema = "public")
@IdClass(PractisingSpecialityId.class)
public class PractisingSpeciality implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "df_availability_id", referencedColumnName = "df_availability_id")
    private DoctorFacilityAvailability doctorFacilityAvailability;

    @Id
    @ManyToOne
    @JoinColumn(name = "speciality_master_id", referencedColumnName = "speciality_master_id")
    private SpecialityMaster specialityMaster;

}
