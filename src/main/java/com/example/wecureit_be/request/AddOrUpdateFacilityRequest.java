package com.example.wecureit_be.request;

import java.util.List;

import lombok.Data;

@Data
public class AddOrUpdateFacilityRequest {
    String facilityMasterId;
    String facilityName;
    Integer noOfRooms;
    String facilityStreet;
    String stateCode;
    String stateName;
    List<String> specialityList;
    List<Object> roomDetails;
}
