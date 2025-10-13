package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.DoctorMasterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    DoctorMasterImpl doctorMasterImpl;

    @PostMapping(value="/addOrUpdate")
    public DoctorMaster addOrUpdate (@RequestBody DoctorMaster doctorMaster){
        return doctorMasterImpl.addOrUpdate(doctorMaster);
    }

    @GetMapping(value="/getById")
    public DoctorMaster getById(@RequestParam Integer doctorId) {
        return doctorMasterImpl.getById(doctorId);
    }

}
