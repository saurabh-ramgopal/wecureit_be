package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.AdminMaster;
import com.example.wecureit_be.repository.AdminMasterRepository;
import com.example.wecureit_be.request.AdminLoginRequest;
import com.example.wecureit_be.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Service
public class AdminControllerImpl {

    @Autowired
    AdminMasterRepository adminMasterRepository;

    public LoginResponse checkLoginCredentials(AdminLoginRequest adminLoginRequest) {
        AdminMaster adminMaster = getByEmail(adminLoginRequest.getEmail());

        if(ObjectUtils.isEmpty(adminMaster))
            return new LoginResponse(adminLoginRequest.getEmail(), null, "FAIL", "This admin does not exist in system.");

        if(Objects.equals(adminMaster.getAdminPassword(), adminLoginRequest.getPassword())) {
            return new LoginResponse(adminLoginRequest.getEmail(), null, "PASS", "LOGIN_SUCCESSFUL");
        }
        else{
            return new LoginResponse(adminLoginRequest.getEmail(), null, "FAIL","Password incorrect, please check credentials.");
        }
    }

    public AdminMaster getByEmail(String adminEmail) {
        return adminMasterRepository.getAdminByEmail(adminEmail);
    }
}
