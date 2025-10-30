package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientControllerImpl;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/patient")
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

    @PostMapping(value="/register")
    public ResponseEntity<?> newRegistration(
            @RequestBody PatientRegistrationRequest patientRegistrationRequest,
            HttpServletRequest request) {
        try {
            log.info("Registration request received for email: {}", patientRegistrationRequest.getEmail());
            
            // Get Firebase UID from the authenticated request
            // This is set by FirebaseAuthFilter after verifying the JWT
            String firebaseUid = (String) request.getAttribute("firebaseUid");
            
            log.info("Firebase UID from request attribute: {}", firebaseUid);
            
            if (firebaseUid == null) {
                log.error("Firebase UID not found in request attributes");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "ERROR");
                errorResponse.put("message", "Firebase authentication required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            PatientMaster patient = patientControllerImpl.newRegistration(patientRegistrationRequest, firebaseUid);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Patient registered successfully");
            response.put("patient", patient);
            
            log.info("Patient registered successfully with ID: {}", patient.getPatientMasterId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
