package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.request.LoginRequest;
import com.example.wecureit_be.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class CommonControllerImpl {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @Autowired
    PatientControllerImpl patientControllerImpl;

    public LoginResponse checkLoginCredentials(LoginRequest loginRequest) {

        log.info("checking credentials for email:{} and type :{}", loginRequest.getEmail(), loginRequest.getType());
        PatientMaster patientMaster = new PatientMaster();
        DoctorMaster doctorMaster = new DoctorMaster();
        String password;
        String email;

        if(loginRequest.getType().equals("patient")){
            patientMaster = patientControllerImpl.getByEmail(loginRequest.getEmail());
        }
        else {
            doctorMaster = doctorControllerImpl.getByEmail(loginRequest.getEmail());
        }

        //if db fetch is empty, user does not exist
        if(ObjectUtils.isEmpty(patientMaster) || ObjectUtils.isEmpty(doctorMaster)){
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "USER_NOT_FOUND");
        }
        else if (!ObjectUtils.isEmpty(patientMaster.getPatientMasterId())) {
            password = patientMaster.getPatientPassword();
            email = patientMaster.getPatientEmail();
        }
        else {
            password = doctorMaster.getDoctorPassword();
            email = doctorMaster.getDoctorEmail();
        }

        //if hashed password matches or not
        if(loginRequest.getEmail().equals(email)
                && loginRequest.getPassword().equals(password)){
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL");
        }
        else{
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL","PASSWORD_INCORRECT");
        }
    }
}
