package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class DeleteFacilityRequest {
    String facilityMasterId;
    Boolean isActive;
}
