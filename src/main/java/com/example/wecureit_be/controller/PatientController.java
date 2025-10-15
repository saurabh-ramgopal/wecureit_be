package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientControllerImpl;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> newRegistration (@RequestBody PatientRegistrationRequest patientRegistrationRequest){
        // if email already exists, return 409 Conflict
        if (patientControllerImpl.emailExists(patientRegistrationRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        PatientMaster saved = patientControllerImpl.newRegistration(patientRegistrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

}
