package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.repository.PatientMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientMasterImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;

    public PatientMaster addOrUpdate(PatientMaster patientMaster) {
        return patientMasterRepository.save(patientMaster);
    }

    public PatientMaster getById(String patientId) {
        return patientMasterRepository.getPatientById(patientId);
    }
}
