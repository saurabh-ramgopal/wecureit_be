package com.example.wecureit_be.request;

import lombok.Data;

import java.util.List;

@Data
public class AddOrUpdateFacilityRequest {
    String facilityMasterId;
    String facilityName;
    Integer noOfRooms;
    String facilityStreet;
    String facilityCity;
    List<String> specialityList;
}
