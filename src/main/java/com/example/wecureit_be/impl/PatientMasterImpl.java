package com.example.wecureit_be.impl;

import com.example.wecureit_be.repository.PatientMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientMasterImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;
}
