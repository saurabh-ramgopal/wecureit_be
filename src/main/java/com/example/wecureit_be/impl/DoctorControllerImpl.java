package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.DoctorSpecialityMapping;
import com.example.wecureit_be.entity.SpecialityMaster;
import com.example.wecureit_be.entity.StateMaster;
import com.example.wecureit_be.repository.DoctorMasterRepository;
import com.example.wecureit_be.repository.DoctorSpecialityMappingRepository;
import com.example.wecureit_be.repository.SpecialityMasterRepository;
import com.example.wecureit_be.repository.StateMasterRepository;
import com.example.wecureit_be.request.AddDoctorRequest;
import com.example.wecureit_be.request.DeleteDoctorRequest;
import com.example.wecureit_be.request.DoctorStateSpeciality;
import com.example.wecureit_be.response.DoctorDetails;
import com.example.wecureit_be.response.DoctorSpecialityDetails;
import com.example.wecureit_be.response.DoctorStateDetails;
import com.example.wecureit_be.utilities.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorControllerImpl {

    @Autowired
    DoctorMasterRepository doctorMasterRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    @Autowired
    DoctorSpecialityMappingRepository doctorSpecialityMappingRepository;

    @Autowired
    StateMasterRepository stateMasterRepository;

    public List<DoctorDetails> getAllDoctors(){
        List<DoctorMaster> doctorMasterList = doctorMasterRepository.findAll();
        List<DoctorDetails> listOfDoctorDetails = new ArrayList<>();
        for(DoctorMaster doctorMaster:doctorMasterList){
            List<DoctorSpecialityMapping> listOfSpeciality =
                    doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorMaster.getDoctorMasterId());
            listOfDoctorDetails.add(prepareDocResponse(doctorMaster, listOfSpeciality));
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

        return updateDoctorStateSpecialities(doctorMaster.getDoctorMasterId(),
                addDoctorRequest.getDoctorStateSpeciality());

    }

    public DoctorDetails updateDoctorStateSpecialities(Integer doctorId, List<DoctorStateSpeciality> listOfDoctorStateSpecialities){
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(doctorId);

        int rows = doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(doctorId);

        for(DoctorStateSpeciality eachStateSpeciality : listOfDoctorStateSpecialities){

            StateMaster stateMaster = stateMasterRepository.getStateById(eachStateSpeciality.getStateCode());

            for(String eachSpeciality : eachStateSpeciality.getSpecialityList()){
                SpecialityMaster specialityMaster = specialityMasterRepository.getSpecialityById(eachSpeciality);
                DoctorSpecialityMapping doctorSpecialityMapping = new DoctorSpecialityMapping();
                doctorSpecialityMapping.setDoctorMaster(doctorMaster);
                doctorSpecialityMapping.setSpecialityMaster(specialityMaster);
                doctorSpecialityMapping.setStateMaster(stateMaster);
                doctorSpecialityMappingRepository.save(doctorSpecialityMapping);
            }
        }

        List<DoctorSpecialityMapping> listOfSpeciality =
                doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorId);

        return prepareDocResponse(doctorMaster, listOfSpeciality);
    }

    public DoctorMaster deleteDoctor(DeleteDoctorRequest deleteDoctorRequest){
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(deleteDoctorRequest.getDoctorMasterId());
        doctorMaster.setIsActive(deleteDoctorRequest.getIsActive());
        return doctorMasterRepository.save(doctorMaster);
    }

    public DoctorDetails getById(Integer doctorId) {
        DoctorMaster doctorMaster =  doctorMasterRepository.getDoctorById(doctorId);

        List<DoctorSpecialityMapping> listOfSpeciality =
                doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorId);
        return prepareDocResponse(doctorMaster, listOfSpeciality);
    }


    //todo
    public DoctorDetails prepareDocResponse (DoctorMaster doctorMaster, List<DoctorSpecialityMapping> list){

        List<DoctorStateDetails> doctorStateSpecialityList = new ArrayList<>();

        Map<StateMaster, List<SpecialityMaster>> stateSpeciality = list.stream()
                .collect(Collectors.groupingBy(
                        DoctorSpecialityMapping::getStateMaster,
                        Collectors.mapping(DoctorSpecialityMapping::getSpecialityMaster, Collectors.toList())
                ));

        for (Map.Entry<StateMaster, List<SpecialityMaster>> entry : stateSpeciality.entrySet()) {
            DoctorStateDetails doctorStateSpeciality = new DoctorStateDetails();
            doctorStateSpeciality.setStateCode(entry.getKey().getStateCode());
            doctorStateSpeciality.setStateName(entry.getKey().getStateName());

            List<DoctorSpecialityDetails> strings = new ArrayList<>();
            for(SpecialityMaster specialityMaster : entry.getValue()){
                DoctorSpecialityDetails doctorSpecialityDetails = new DoctorSpecialityDetails();
                doctorSpecialityDetails.setSpecialityId(specialityMaster.getSpecialityMasterId());
                doctorSpecialityDetails.setSpecialityName(specialityMaster.getSpecialityName());
                strings.add(doctorSpecialityDetails);
            }
            doctorStateSpeciality.setStateSpecialities(strings);
            doctorStateSpecialityList.add(doctorStateSpeciality);
        }

        return new DoctorDetails(doctorMaster.getDoctorMasterId(),
                doctorMaster.getDoctorName(), doctorMaster.getDoctorEmail(),
                doctorMaster.getDoctorGender(), doctorStateSpecialityList);
    }

    public DoctorMaster getByEmail(String doctorEmail) {
        return doctorMasterRepository.getDoctorByEmail(doctorEmail);
    }
}
