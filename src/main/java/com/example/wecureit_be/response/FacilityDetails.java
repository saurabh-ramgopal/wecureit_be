package com.example.wecureit_be.response;

import com.example.wecureit_be.entity.SpecialityMaster;
import lombok.Data;

import java.util.List;

@Data
public class FacilityDetails {
    public String facilityMasterId;
    public String facilityName;
    public Integer noOfRooms;
    public String facilityStreet;
    public String facilityCity;
    public Boolean isActive;
    public List<SpecialityMaster> speciality;
}
