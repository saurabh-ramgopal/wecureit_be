package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "facility_master", schema = "public")
public class FacilityMaster {

    @Id
    @Column(name = "facility_master_id")
    public String facilityMasterId;

    @Column(name = "facility_name")
    public String facilityName;

    @Column(name = "no_of_rooms")
    public Integer noOfRooms;

    @Column(name = "facility_street")
    public String facilityStreet;

    @Column(name = "facility_city")
    public String facilityCity;

    @Column(name = "is_active")
    public Boolean isActive;
}
