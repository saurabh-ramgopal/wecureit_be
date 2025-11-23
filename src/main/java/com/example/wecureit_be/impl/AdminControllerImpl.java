package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.AdminMaster;
import com.example.wecureit_be.repository.AdminMasterRepository;
import com.example.wecureit_be.request.AdminRegisterRequest;
import com.example.wecureit_be.utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AdminControllerImpl {

    @Autowired
    AdminMasterRepository adminMasterRepository;

    @SneakyThrows
    public AdminMaster registerAdmin (AdminRegisterRequest adminRegisterRequest) {
        AdminMaster adminMaster = new AdminMaster();
        adminMaster.setAdminMasterId(Utils.generateUUID());
        adminMaster.setAdminEmail(adminRegisterRequest.getEmail());
        adminMaster.setAdminName(adminRegisterRequest.getName());
        adminMasterRepository.save(adminMaster);

        Map<String, Object> claims = new HashMap<>();
        claims.put("adminMasterId", adminMaster.getAdminMasterId());
        claims.put("role", "admin");
        FirebaseAuth.getInstance().setCustomUserClaims(adminRegisterRequest.getFirebaseUid(), claims);

        System.out.println("Claims updated for user: " + adminRegisterRequest.getFirebaseUid());

        return adminMaster;
    }

    public AdminMaster getByEmail(String adminEmail) {
        return adminMasterRepository.getAdminByEmail(adminEmail);
    }
}
