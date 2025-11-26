package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.repository.PatientMasterRepository;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.utilities.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.wecureit_be.request.PatientUpdateRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PatientControllerImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;

    public PatientMaster addOrUpdate(PatientMaster patientMaster) {
        // Only allow updating email and phone for an existing patient.
        if (patientMaster == null || patientMaster.getPatientMasterId() == null) {
            throw new IllegalArgumentException("patientMaster and patientMaster.patientMasterId must be provided for update");
        }

        PatientMaster existing = patientMasterRepository.getPatientById(patientMaster.getPatientMasterId());
        if (existing == null) {
            throw new IllegalArgumentException("Patient with id " + patientMaster.getPatientMasterId() + " does not exist");
        }

        // Only update allowed fields
        if (patientMaster.getPatientEmail() != null) {
            existing.setPatientEmail(patientMaster.getPatientEmail());
        }
        if (patientMaster.getPatientPhone() != null) {
            existing.setPatientPhone(patientMaster.getPatientPhone());
        }

        return patientMasterRepository.save(existing);
    }

    public PatientMaster getById(Integer patientId) {
        return patientMasterRepository.getPatientById(patientId);
    }

    public PatientMaster getByEmail(String patientEmail) {
        return patientMasterRepository.getPatientByEmail(patientEmail);
    }

    @SneakyThrows
    public PatientMaster newRegistration(PatientRegistrationRequest patientRegistrationRequest){
        log.info("adding new patient details:{}", patientRegistrationRequest.getName());
        PatientMaster patientMaster = new PatientMaster();
        patientMaster.setPatientMasterId(Utils.generateFiveDigitNumber()); //to-do change accordingly when auto-increment implemented
//        patientMaster.setPatientMasterId(Utils.generateUUID()); to be added if we are using String as PK
        patientMaster.setPatientName(patientRegistrationRequest.getName());
        patientMaster.setPatientEmail(patientRegistrationRequest.getEmail());
        patientMaster.setPatientDob(patientRegistrationRequest.getDob());
        patientMaster.setPatientGender(patientRegistrationRequest.getGender());
        patientMaster.setPatientPhone(patientRegistrationRequest.getPhone());
        patientMaster.setPatientAddress(patientRegistrationRequest.getAddress());
        patientMaster = patientMasterRepository.save(patientMaster);

        Map<String, Object> claims = new HashMap<>();
        claims.put("patientMasterId", patientMaster.getPatientMasterId());
        claims.put("role", "patient");
        FirebaseAuth.getInstance().setCustomUserClaims(patientRegistrationRequest.getFirebaseUid(), claims);

        System.out.println("Claims updated for user: " + patientRegistrationRequest.getFirebaseUid());
        return patientMaster;

    }

    public PatientMaster updatePatient(Integer patientId, PatientUpdateRequest updateRequest) {
        if (patientId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "patientId is required");
        }
        PatientMaster existing = patientMasterRepository.getPatientById(patientId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        }

        if (updateRequest.getEmail() != null) {
            existing.setPatientEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhone() != null) {
            existing.setPatientPhone(updateRequest.getPhone());
        }

        return patientMasterRepository.save(existing);
    }

}
