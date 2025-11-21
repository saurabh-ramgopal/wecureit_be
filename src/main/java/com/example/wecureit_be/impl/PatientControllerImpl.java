package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.repository.PatientMasterRepository;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.utilities.Utils;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PatientControllerImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;

    public PatientMaster addOrUpdate(PatientMaster patientMaster) {
        return patientMasterRepository.save(patientMaster);
    }

    public PatientMaster getById(Integer patientId) {
        return patientMasterRepository.getPatientById(patientId);
    }

    public PatientMaster getByEmail(String patientEmail) {
        return patientMasterRepository.getPatientByEmail(patientEmail);
    }

    public PatientMaster newRegistration(PatientRegistrationRequest patientRegistrationRequest) throws FirebaseAuthException {
        log.info("adding new patient details:{}", patientRegistrationRequest.getName());
        PatientMaster patientMaster = new PatientMaster();
        patientMaster.setPatientMasterId(Utils.generateFiveDigitNumber()); //to-do change accordingly when auto-increment implemented
//        patientMaster.setPatientMasterId(Utils.generateUUID()); to be added if we are using String as PK
        patientMaster.setPatientName(patientRegistrationRequest.getName());
        patientMaster.setPatientEmail(patientRegistrationRequest.getEmail());
        patientMaster.setPatientDob(patientRegistrationRequest.getDob());
        patientMaster.setPatientGender(patientRegistrationRequest.getGender());
        patientMaster.setPatientPhone(patientRegistrationRequest.getPhone());
        patientMaster = patientMasterRepository.save(patientMaster);

        Map<String, Object> claims = new HashMap<>();
        claims.put("patientMasterId", patientMaster.getPatientMasterId());
        FirebaseAuth.getInstance().setCustomUserClaims(patientRegistrationRequest.getFirebaseUid(), claims);

        System.out.println("Claims updated for user: " + patientRegistrationRequest.getFirebaseUid());
        return patientMaster;

    }

}
