package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.DoctorMasterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    DoctorMasterImpl doctorMasterImpl;

    @PostMapping(value="/addOrUpdate")
    public DoctorMaster addOrUpdate (@RequestBody DoctorMaster doctorMaster){
        return doctorMasterImpl.addOrUpdate(doctorMaster);
    }

    @GetMapping(value="/get")
    public List<DoctorMaster> getAllDoctors() {
        return doctorMasterImpl.getAllDoctors();
    }

}
