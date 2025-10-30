package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.SpecialityMaster;
import com.example.wecureit_be.repository.SpecialityMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialityControllerImpl {

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    public List<SpecialityMaster> getAllSpeciality(){
        return specialityMasterRepository.getAllSpeciality();
    }
}
