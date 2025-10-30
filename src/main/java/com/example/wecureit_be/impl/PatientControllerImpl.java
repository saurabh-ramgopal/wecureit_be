package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.repository.PatientMasterRepository;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.utilities.FirebaseService;
import com.example.wecureit_be.utilities.Utils;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientControllerImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;
    
    @Autowired
    FirebaseService firebaseService;

    public PatientMaster addOrUpdate(PatientMaster patientMaster) {
        return patientMasterRepository.save(patientMaster);
    }

    public PatientMaster getById(Integer patientId) {
        return patientMasterRepository.getPatientById(patientId);
    }

    public PatientMaster getByEmail(String patientEmail) {
        return patientMasterRepository.getPatientByEmail(patientEmail);
    }

    public PatientMaster newRegistration(PatientRegistrationRequest patientRegistrationRequest, String firebaseUid) {
        log.info("adding new patient details:{} with Firebase UID: {}", patientRegistrationRequest.getName(), firebaseUid);
        
        // Check if user already exists in database
        PatientMaster existingPatient = patientMasterRepository.getPatientByEmail(patientRegistrationRequest.getEmail());
        if (existingPatient != null) {
            throw new RuntimeException("Patient with this email already exists in database");
        }
        
        // Create patient record in database
        // Firebase user is already created by the frontend
        PatientMaster patientMaster = new PatientMaster();
        patientMaster.setPatientMasterId(Utils.generateFiveDigitNumber());
        patientMaster.setPatientName(patientRegistrationRequest.getName());
        patientMaster.setPatientEmail(patientRegistrationRequest.getEmail());
        // Store Firebase UID instead of password
        patientMaster.setPatientPassword(firebaseUid); // We'll use this field to store Firebase UID
        patientMaster.setPatientDob(patientRegistrationRequest.getDob());
        
        log.info("Saved patient to database with Firebase UID: {}", firebaseUid);
        
        return patientMasterRepository.save(patientMaster);
    }

}
