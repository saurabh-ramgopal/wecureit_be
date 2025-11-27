package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientControllerImpl;
import com.example.wecureit_be.request.PatientBookingRequest;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.request.PatientUpdateRequest;
import com.example.wecureit_be.response.PatientBookingL1Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/patient")
public class PatientController {

    //private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    PatientControllerImpl patientControllerImpl;

    // @Deprecated
    @PostMapping(value="/addOrUpdate")
    public PatientMaster addOrUpdate (@RequestBody PatientMaster patientMaster){
        //log.warn("Deprecated endpoint /patient/addOrUpdate called - prefer PATCH /patient/{id}");
        return patientControllerImpl.addOrUpdate(patientMaster);
    }

    @PatchMapping(value="/{patientId}")
    public PatientMaster patchUpdate(@PathVariable Integer patientId, @RequestBody @Valid PatientUpdateRequest updateRequest){
        return patientControllerImpl.updatePatient(patientId, updateRequest);
    }

    @GetMapping(value="/getById")
    public PatientMaster getById (@RequestParam Integer patientId){
        return patientControllerImpl.getById(patientId);
    }

    @PostMapping(value="/registration")
    public PatientMaster newRegistration (@RequestBody PatientRegistrationRequest patientRegistrationRequest) {
        return patientControllerImpl.newRegistration(patientRegistrationRequest);
    }

    @PostMapping(value="/bookAppointment/l1")
    public PatientBookingL1Response appointmentBookingL1(@RequestBody PatientBookingRequest patientBookingRequest){
        return patientControllerImpl.appointmentBookingL1(patientBookingRequest);
    }

    @PostMapping(value="/bookAppointment/l2")
    public PatientBookingL1Response appointmentBookingL2(@RequestBody PatientBookingRequest patientBookingRequest){
        return patientControllerImpl.appointmentBookingL2(patientBookingRequest);
    }

}
