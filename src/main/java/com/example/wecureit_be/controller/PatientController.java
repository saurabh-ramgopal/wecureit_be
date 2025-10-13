package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientControllerImpl patientControllerImpl;

    @PostMapping(value="/addOrUpdate")
    public PatientMaster addOrUpdate (@RequestBody PatientMaster patientMaster){
        return patientControllerImpl.addOrUpdate(patientMaster);
    }

    @GetMapping(value="/getById")
    public PatientMaster getById (@RequestParam String patientId){
        return patientControllerImpl.getById(patientId);
    }

}
