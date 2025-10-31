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

    public LoginResponse checkLoginCredentials(CommonLoginRequest commonLoginRequest, String firebaseUid) {

        log.info("checking credentials for email:{} and type :{}", commonLoginRequest.getEmail(), commonLoginRequest.getType());
        
        // If Firebase UID is provided, use Firebase authentication
        if (firebaseUid != null && !firebaseUid.isEmpty()) {
            return checkFirebaseLogin(commonLoginRequest, firebaseUid);
        }
        
        // Otherwise, use traditional password authentication
        return checkPasswordLogin(commonLoginRequest);
    }
    
    private LoginResponse checkFirebaseLogin(CommonLoginRequest commonLoginRequest, String firebaseUid) {
        log.info("Firebase login for email:{} with UID:{}", commonLoginRequest.getEmail(), firebaseUid);
        
        PatientMaster patientMaster = null;
        DoctorMaster doctorMaster = null;
        
        if(commonLoginRequest.getType().equals("patient")){
            patientMaster = patientControllerImpl.getByEmail(commonLoginRequest.getEmail());
            if (patientMaster != null && firebaseUid.equals(patientMaster.getPatientPassword())) {
                 return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL", patientMaster.getPatientName());
            }
        } else {
            doctorMaster = doctorControllerImpl.getByEmail(commonLoginRequest.getEmail());
            if (doctorMaster != null && firebaseUid.equals(doctorMaster.getDoctorPassword())) {
                 return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL", doctorMaster.getDoctorName());
            }
        }
        
           return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "FAIL", "Firebase authentication failed", null);
    }
    
    private LoginResponse checkPasswordLogin(CommonLoginRequest commonLoginRequest) {
        PatientMaster patientMaster = new PatientMaster();
        DoctorMaster doctorMaster = new DoctorMaster();
        String password;
        String email;
        String name;

        if(commonLoginRequest.getType().equals("patient")){
            patientMaster = patientControllerImpl.getByEmail(commonLoginRequest.getEmail());
        }
        else {
            doctorMaster = doctorControllerImpl.getByEmail(commonLoginRequest.getEmail());
        }

        //if db fetch is empty, user does not exist
        if(ObjectUtils.isEmpty(patientMaster) || ObjectUtils.isEmpty(doctorMaster)){
              return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "FAIL", "The user does not exist in system. Please sign up.", null);
        }
        else if (!ObjectUtils.isEmpty(patientMaster.getPatientMasterId())) {
            password = patientMaster.getPatientPassword();
            email = patientMaster.getPatientEmail();
              name = patientMaster.getPatientName();
        }
        else {
            password = doctorMaster.getDoctorPassword();
            email = doctorMaster.getDoctorEmail();
              name = doctorMaster.getDoctorName();
        }

        //if hashed password matches or not
        if(commonLoginRequest.getEmail().equals(email)
                && commonLoginRequest.getPassword().equals(password)){
              return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL", name);
        }
        else{
              return new LoginResponse(commonLoginRequest.getEmail(), commonLoginRequest.getType(), "FAIL","Password incorrect, please check credentials.", null);
        }
    }
}
