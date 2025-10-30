package com.example.wecureit_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "facility_speciality_mapping", schema = "public")
@IdClass(FacilitySpecialityMappingId.class)
public class FacilitySpecialityMapping implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "facility_master_id", referencedColumnName = "facility_master_id")
    private FacilityMaster facilityMaster;

    @Id
    @ManyToOne
    @JoinColumn(name = "speciality_master_id", referencedColumnName = "speciality_master_id")
    private SpecialityMaster specialityMaster;

}
