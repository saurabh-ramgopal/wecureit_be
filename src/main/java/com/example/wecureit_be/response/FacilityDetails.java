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
    public String stateCode;
    public String stateName;
    public Boolean isActive;
    public List<SpecialityMaster> speciality;
    // Optional per-room details (populated when available or on add/update)
    public List<com.example.wecureit_be.response.RoomDetail> roomDetails;
}
