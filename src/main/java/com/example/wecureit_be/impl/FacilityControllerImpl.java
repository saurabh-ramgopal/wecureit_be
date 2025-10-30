package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.*;
import com.example.wecureit_be.repository.FacilityMasterRepository;
import com.example.wecureit_be.repository.FacilitySpecialityMappingRepository;
import com.example.wecureit_be.repository.SpecialityMasterRepository;
import com.example.wecureit_be.request.AddOrUpdateFacilityRequest;
import com.example.wecureit_be.request.DeleteFacilityRequest;
import com.example.wecureit_be.response.FacilityDetails;
import com.example.wecureit_be.utilities.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class FacilityControllerImpl {

    @Autowired
    FacilityMasterRepository facilityMasterRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    @Autowired
    FacilitySpecialityMappingRepository facilitySpecialityMappingRepository;


    public List<FacilityMaster> getAllFacility(){
        return facilityMasterRepository.getAllFacility();
    }


    public FacilityDetails addOrUpdateFacility(AddOrUpdateFacilityRequest addOrUpdateFacilityRequest) {

        FacilityMaster facilityMaster;
        if(ObjectUtils.isEmpty(addOrUpdateFacilityRequest.getFacilityMasterId())) {
            facilityMaster = new FacilityMaster();
            facilityMaster.setFacilityMasterId(Utils.generateUUID());
        }
        else{
            facilityMaster = facilityMasterRepository.getFacilityById(addOrUpdateFacilityRequest.getFacilityMasterId());
        }

        facilityMaster.setFacilityName(addOrUpdateFacilityRequest.getFacilityName());
        facilityMaster.setFacilityStreet(addOrUpdateFacilityRequest.getFacilityStreet());
        facilityMaster.setFacilityCity(addOrUpdateFacilityRequest.getFacilityCity());
        facilityMaster.setNoOfRooms(addOrUpdateFacilityRequest.getNoOfRooms());
        facilityMaster.setIsActive(true);
        facilityMasterRepository.save(facilityMaster);

        int rows = facilitySpecialityMappingRepository.deleteFacilityAllSpeciality(facilityMaster.getFacilityMasterId());

        for(String eachSpeciality : addOrUpdateFacilityRequest.getSpecialityList() ){
            SpecialityMaster specialityMaster = specialityMasterRepository.getSpecialityById(eachSpeciality);
            FacilitySpecialityMapping facilitySpecialityMapping = new FacilitySpecialityMapping();
            facilitySpecialityMapping.setFacilityMaster(facilityMaster);
            facilitySpecialityMapping.setSpecialityMaster(specialityMaster);
            facilitySpecialityMappingRepository.save(facilitySpecialityMapping);
        }

        List<SpecialityMaster> specialityMaster = getSpecialityByFacilityId(facilityMaster.getFacilityMasterId());

        FacilityDetails facilityDetails = new FacilityDetails();

        BeanUtils.copyProperties(facilityMaster, facilityDetails);
        facilityDetails.setSpeciality(specialityMaster);

        return facilityDetails;
    }

    public List<SpecialityMaster> getSpecialityByFacilityId(String facilityMasterId) {
        List<FacilitySpecialityMapping> listOfSpeciality =  facilitySpecialityMappingRepository.getSpecialityByFacilityId(facilityMasterId);
        List<SpecialityMaster> specialities = new ArrayList<>();

        for(FacilitySpecialityMapping speciality: listOfSpeciality){
            specialities.add(speciality.getSpecialityMaster());
        }

        return specialities;
    }

    @Transactional
    public FacilityMaster deleteFacility(DeleteFacilityRequest deleteFacilityRequest) {
        FacilityMaster facilityMaster = facilityMasterRepository.getFacilityById(deleteFacilityRequest.getFacilityMasterId());
        facilityMaster.setIsActive(deleteFacilityRequest.getIsActive());
        return facilityMasterRepository.save(facilityMaster);

    }
}
