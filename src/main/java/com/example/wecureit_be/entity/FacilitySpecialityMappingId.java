package com.example.wecureit_be.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySpecialityMappingId implements Serializable {
    private String facilityMaster;
    private String specialityMaster;
}