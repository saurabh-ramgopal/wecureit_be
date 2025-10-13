package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientMasterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientMasterImpl patientMasterImpl;

    @PostMapping(value="/addOrUpdate")
    public PatientMaster addOrUpdate (@RequestBody PatientMaster patientMaster){
        return patientMasterImpl.addOrUpdate(patientMaster);
    }

    @GetMapping(value="/getById")
    public PatientMaster getById (@RequestParam Integer patientId){
        return patientMasterImpl.getById(patientId);
    }

}
