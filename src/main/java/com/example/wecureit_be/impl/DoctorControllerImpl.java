package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.DoctorSpecialityMapping;
import com.example.wecureit_be.entity.SpecialityMaster;
import com.example.wecureit_be.repository.DoctorMasterRepository;
import com.example.wecureit_be.repository.DoctorSpecialityMappingRepository;
import com.example.wecureit_be.repository.SpecialityMasterRepository;
import com.example.wecureit_be.request.AddDoctorRequest;
import com.example.wecureit_be.request.DeleteDoctorRequest;
import com.example.wecureit_be.request.DoctorSpecialityRequest;
import com.example.wecureit_be.response.DoctorDetails;
import com.example.wecureit_be.utilities.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorControllerImpl {

    @Autowired
    DoctorMasterRepository doctorMasterRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    @Autowired
    DoctorSpecialityMappingRepository doctorSpecialityMappingRepository;

    public List<DoctorDetails> getAllDoctors(){
        List<DoctorMaster> doctorMasterList = doctorMasterRepository.findAll();
        List<DoctorDetails> listOfDoctorDetails = new ArrayList<>();
        for(DoctorMaster doctorMaster:doctorMasterList){
            List<SpecialityMaster> list = getDoctorSpecialityByDoctorId(doctorMaster.getDoctorMasterId());
            listOfDoctorDetails.add(prepareDocResponse(doctorMaster, list));
        }
        return listOfDoctorDetails;
    }

    public DoctorDetails addDoctor(AddDoctorRequest addDoctorRequest){
        DoctorMaster doctorMaster = new DoctorMaster();
        doctorMaster.setDoctorMasterId(Utils.generateFiveDigitNumber());
        doctorMaster.setDoctorName(addDoctorRequest.getDoctorName());
        doctorMaster.setDoctorGender(addDoctorRequest.getDoctorGender());
        doctorMaster.setDoctorPassword(addDoctorRequest.getDoctorPassword());
        doctorMaster.setDoctorEmail(addDoctorRequest.getDoctorEmail());
        doctorMaster.setIsActive(true);
        doctorMasterRepository.save(doctorMaster);

        DoctorSpecialityRequest doctorSpecialityRequest = new DoctorSpecialityRequest();
        doctorSpecialityRequest.setDoctorMasterId(doctorMaster.getDoctorMasterId());
        doctorSpecialityRequest.setSpecialityList(addDoctorRequest.getSpecialityList());
        return updateDoctorSpeciality(doctorSpecialityRequest);
    }

    public DoctorMaster deleteDoctor(DeleteDoctorRequest deleteDoctorRequest){
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(deleteDoctorRequest.getDoctorMasterId());
        doctorMaster.setIsActive(deleteDoctorRequest.getIsActive());
        return doctorMasterRepository.save(doctorMaster);
    }

    public DoctorDetails updateDoctorSpeciality(DoctorSpecialityRequest doctorSpecialityRequest){
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(doctorSpecialityRequest.getDoctorMasterId());

        int rows = doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(doctorSpecialityRequest.getDoctorMasterId());

        for(String eachSpeciality : doctorSpecialityRequest.getSpecialityList() ){
            SpecialityMaster specialityMaster = specialityMasterRepository.getSpecialityById(eachSpeciality);
            DoctorSpecialityMapping doctorSpecialityMapping = new DoctorSpecialityMapping();
            doctorSpecialityMapping.setDoctorMaster(doctorMaster);
            doctorSpecialityMapping.setSpecialityMaster(specialityMaster);
            doctorSpecialityMappingRepository.save(doctorSpecialityMapping);
        }

        List<SpecialityMaster> list = getDoctorSpecialityByDoctorId(doctorMaster.getDoctorMasterId());

        return prepareDocResponse(doctorMaster, list);
    }

    public DoctorDetails getById(Integer doctorId) {
        DoctorMaster doctorMaster =  doctorMasterRepository.getDoctorById(doctorId);
        List<SpecialityMaster> list = getDoctorSpecialityByDoctorId(doctorId);
        return prepareDocResponse(doctorMaster, list);
    }

    public DoctorDetails prepareDocResponse (DoctorMaster doctorMaster, List<SpecialityMaster> list){
        return new DoctorDetails(doctorMaster.getDoctorMasterId(),
                doctorMaster.getDoctorName(), doctorMaster.getDoctorEmail(),
                doctorMaster.getDoctorGender(), list);
    }

    public List<SpecialityMaster> getDoctorSpecialityByDoctorId(Integer doctorId) {
        List<DoctorSpecialityMapping> listOfSpeciality =  doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorId);
        List<SpecialityMaster> specialities = new ArrayList<>();

        for(DoctorSpecialityMapping speciality: listOfSpeciality){
            specialities.add(speciality.getSpecialityMaster());
        }

        return specialities;
    }

    public DoctorMaster getByEmail(String doctorEmail) {
        return doctorMasterRepository.getDoctorByEmail(doctorEmail);
    }
}
