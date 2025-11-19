package com.example.wecureit_be.response;

import com.example.wecureit_be.entity.FacilityMaster;
import com.example.wecureit_be.entity.StateMaster;

import lombok.Value;

@Value
public class DoctorFacilities {
    private String facilityId;
    private String facilityName;
    private String stateCode;
    //private String city;
    private Integer noOfRooms;
    private Boolean isActive;

    public static DoctorFacilities fromEntity(FacilityMaster f) {
        return new DoctorFacilities(
                f.getFacilityMasterId(),
                f.getFacilityName(),
                f.getStateCode().getStateCode(),
                f.getNoOfRooms(),
                f.getIsActive()
        );
    }
    
}
