package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.AdminMaster;
import com.example.wecureit_be.repository.AdminMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminControllerImpl {

    @Autowired
    AdminMasterRepository adminMasterRepository;

    public AdminMaster getById(Integer adminId) {
        return adminMasterRepository.getAdminById(adminId);
    }

    public AdminMaster getByEmail(String email) {
        return adminMasterRepository.getAdminByEmail(email);
    }

}
