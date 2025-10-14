package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientControllerImpl;
import com.example.wecureit_be.request.PatientRegistrationRequest;
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
    public PatientMaster getById (@RequestParam Integer patientId){
        return patientControllerImpl.getById(patientId);
    }

    @PostMapping(value="/registration")
    public PatientMaster newRegistration (@RequestBody PatientRegistrationRequest patientRegistrationRequest){
        return patientControllerImpl.newRegistration(patientRegistrationRequest);
    }

}
