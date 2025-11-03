package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.FacilityMaster;
import com.example.wecureit_be.entity.SpecialityMaster;
import com.example.wecureit_be.impl.FacilityControllerImpl;
import com.example.wecureit_be.impl.SpecialityControllerImpl;
import com.example.wecureit_be.request.AddOrUpdateFacilityRequest;
import com.example.wecureit_be.request.CommonLoginRequest;
import com.example.wecureit_be.impl.CommonControllerImpl;
import com.example.wecureit_be.request.DeleteFacilityRequest;
import com.example.wecureit_be.response.FacilityDetails;
import com.example.wecureit_be.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    CommonControllerImpl commonControllerImpl;

    @Autowired
    SpecialityControllerImpl specialityControllerImpl;

    @Autowired
    FacilityControllerImpl facilityControllerImpl;

    @PostMapping(value="/login")
    public LoginResponse checkLoginCredentials (@RequestBody CommonLoginRequest commonLoginRequest){
        return commonControllerImpl.checkLoginCredentials(commonLoginRequest);
    }

    @GetMapping(value = "/getSpeciality")
    public List<SpecialityMaster> getSpeciality(){
        return specialityControllerImpl.getAllSpeciality();
    }

    @GetMapping(value = "/getFacility")
    public List<FacilityMaster> getFacility(){
        return facilityControllerImpl.getAllFacility();
    }

    @PostMapping(value = "/facility/addOrUpdate")
    public FacilityDetails addOrUpdateFacility(@RequestBody AddOrUpdateFacilityRequest addOrUpdateFacilityRequest){
        return facilityControllerImpl.addOrUpdateFacility(addOrUpdateFacilityRequest);
    }

    @PostMapping(value = "/deleteFacility")
    public FacilityMaster deleteFacility(@RequestBody DeleteFacilityRequest deleteFacilityRequest){
        return facilityControllerImpl.deleteFacility(deleteFacilityRequest);
    }

}
