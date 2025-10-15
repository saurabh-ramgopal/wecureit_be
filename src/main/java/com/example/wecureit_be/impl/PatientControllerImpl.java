package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.repository.PatientMasterRepository;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.utilities.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientControllerImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public PatientMaster addOrUpdate(PatientMaster patientMaster) {
        return patientMasterRepository.save(patientMaster);
    }

    public PatientMaster getById(Integer patientId) {
        return patientMasterRepository.getPatientById(patientId);
    }

    public PatientMaster getByEmail(String patientEmail) {
        return patientMasterRepository.getPatientByEmail(patientEmail);
    }

    public boolean emailExists(String patientEmail) {
        return getByEmail(patientEmail) != null;
    }

    public PatientMaster newRegistration(PatientRegistrationRequest patientRegistrationRequest) {
        log.info("adding new patient details:{}", patientRegistrationRequest.getName());
        PatientMaster patientMaster = new PatientMaster();
        patientMaster.setPatientMasterId(Utils.generateFiveDigitNumber()); //to-do change accordingly when auto-increment implemented
//        patientMaster.setPatientMasterId(Utils.generateUUID()); to be added if we are using String as PK
        patientMaster.setPatientName(patientRegistrationRequest.getName());
    patientMaster.setPatientEmail(patientRegistrationRequest.getEmail());
    // store encoded password
    patientMaster.setPatientPassword(passwordEncoder.encode(patientRegistrationRequest.getPassword()));
        patientMaster.setPatientDob(patientRegistrationRequest.getDob());
        return patientMasterRepository.save(patientMaster);
    }

}
