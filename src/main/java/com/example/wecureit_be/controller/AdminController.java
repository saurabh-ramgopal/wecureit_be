package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.DoctorMasterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    DoctorMasterImpl doctorMasterImpl;

    @GetMapping(value="/getAllDoctors")
    public List<DoctorMaster> getAllDoctors() {
        return doctorMasterImpl.getAllDoctors();
    }

}
