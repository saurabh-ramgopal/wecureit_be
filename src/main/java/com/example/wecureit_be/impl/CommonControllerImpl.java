package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.request.CommonLoginRequest;
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

    public LoginResponse checkLoginCredentials(CommonLoginRequest commonLoginRequest) {

        log.info("checking credentials for email:{} and type :{}", commonLoginRequest.getEmail(), commonLoginRequest.getType());
        PatientMaster patientMaster = new PatientMaster();
        DoctorMaster doctorMaster = new DoctorMaster();
        String password;
        String email;

        if(commonLoginRequest.getType().equals("patient")){
            patientMaster = patientControllerImpl.getByEmail(commonLoginRequest.getEmail());
        }
        else {
            doctorMaster = doctorControllerImpl.getByEmail(commonLoginRequest.getEmail());
        }

        //if db fetch is empty, user does not exist
        if(ObjectUtils.isEmpty(patientMaster) || ObjectUtils.isEmpty(doctorMaster)){
            return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "FAIL", "The user does not exist in system. Please sign up.");
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
        if(commonLoginRequest.getEmail().equals(email)
                && commonLoginRequest.getPassword().equals(password)){
            return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL");
        }
        else{
            return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "FAIL","Password incorrect, please check credentials.");
        }
    }
}
