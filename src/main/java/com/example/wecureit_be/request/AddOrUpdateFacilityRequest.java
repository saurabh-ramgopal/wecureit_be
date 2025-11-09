package com.example.wecureit_be.request;

import lombok.Data;

import java.util.List;

@Data
public class AddOrUpdateFacilityRequest {
    String facilityMasterId;
    String facilityName;
    Integer noOfRooms;
    String facilityStreet;
    String stateCode;
    String stateName;
    List<String> specialityList;
    // Optional per-room details sent by the FE. Each element is expected to be an
    // object containing keys like roomNumber, roomLabel and specialityList.
    List<Object> roomDetails;
}
