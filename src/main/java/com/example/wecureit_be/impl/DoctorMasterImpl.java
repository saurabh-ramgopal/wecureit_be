package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.repository.DoctorMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorMasterImpl {

    @Autowired
    DoctorMasterRepository doctorMasterRepository;

    public List<DoctorMaster> getAllDoctors(){
        return doctorMasterRepository.findAll();
    }

    public DoctorMaster addOrUpdate(DoctorMaster doctorMaster){
        return doctorMasterRepository.save(doctorMaster);
    }
}
