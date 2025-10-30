package com.example.wecureit_be.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSpecialityMappingId implements Serializable {
    private Integer doctorMaster;
    private String specialityMaster;
}
