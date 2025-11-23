package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.DoctorControllerImpl;
import com.example.wecureit_be.request.AddDoctorAvailabilityRequest;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.response.AddDoctorAvailabilityResponse;
import com.example.wecureit_be.response.DoctorDetails;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.wecureit_be.response.DoctorFacilities;
import java.util.*;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @GetMapping(value="/getById")
    public DoctorDetails getById(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getById(doctorId);
    }

    @PostMapping(value = "/availability/add")
    public AddDoctorAvailabilityResponse addAvailability(@RequestBody AddDoctorAvailabilityRequest addDoctorAvailabilityRequest) {
        return doctorControllerImpl.addAvailability(addDoctorAvailabilityRequest);
    }

    @GetMapping(value="facilities/getById")
    public List<DoctorFacilities> getFacilitiesForDoctor(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getFacilitiesForDoctor(doctorId);
    }

}
