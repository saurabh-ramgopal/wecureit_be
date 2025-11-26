package com.example.wecureit_be.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.DoctorSpecialityMapping;
import com.example.wecureit_be.entity.SpecialityMaster;
import com.example.wecureit_be.entity.StateMaster;
import com.example.wecureit_be.repository.*;
import com.example.wecureit_be.request.AddDoctorRequest;
import com.example.wecureit_be.request.DeleteDoctorRequest;
import com.example.wecureit_be.request.DoctorStateSpeciality;
import com.example.wecureit_be.request.AddDoctorAvailabilityRequest;
import com.example.wecureit_be.request.AddDoctorAvailabilityList;
import com.example.wecureit_be.response.AddDoctorAvailabilityResponse;
import com.example.wecureit_be.entity.DoctorFacilityAvailability;
import com.example.wecureit_be.response.DoctorDetails;
import com.example.wecureit_be.utilities.Utils;
import com.example.wecureit_be.response.DoctorFacilities;
import com.example.wecureit_be.response.DoctorSpecialityDetails;
import com.example.wecureit_be.response.DoctorStateDetails;
import com.google.firebase.auth.FirebaseAuth;
import lombok.SneakyThrows;
import java.util.HashMap;

@Service
public class DoctorControllerImpl {

    @Autowired
    private final FacilityMasterRepository facilityMasterRepository;

    @Autowired
    DoctorMasterRepository doctorMasterRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    @Autowired
    DoctorSpecialityMappingRepository doctorSpecialityMappingRepository;

    @Autowired
    DoctorFacilityAvailabilityRepository doctorFacilityAvailabilityRepository;

    @Autowired
    StateMasterRepository stateMasterRepository;

    DoctorControllerImpl(FacilityMasterRepository facilityMasterRepository) {
        this.facilityMasterRepository = facilityMasterRepository;
    }

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

    @SneakyThrows
    public DoctorDetails addDoctor(AddDoctorRequest addDoctorRequest){
        DoctorMaster doctorMaster = new DoctorMaster();
        doctorMaster.setDoctorMasterId(Utils.generateFiveDigitNumber());
        doctorMaster.setDoctorName(addDoctorRequest.getDoctorName());
        doctorMaster.setDoctorGender(addDoctorRequest.getDoctorGender());
        doctorMaster.setDoctorEmail(addDoctorRequest.getDoctorEmail());
        doctorMaster.setIsActive(true);
        doctorMasterRepository.save(doctorMaster);

        DoctorDetails doctorDetails = updateDoctorStateSpecialities(doctorMaster.getDoctorMasterId(),
                addDoctorRequest.getDoctorStateSpeciality());

        Map<String, Object> claims = new HashMap<>();
        claims.put("doctorMasterId", doctorDetails.getDoctorMasterId());
        claims.put("role", "doctor");
        FirebaseAuth.getInstance().setCustomUserClaims(addDoctorRequest.getFirebaseUid(), claims);

        System.out.println("Claims updated for user: " + addDoctorRequest.getFirebaseUid());

        return doctorDetails;
    }

    public DoctorDetails updateDoctorStateSpecialities(Integer doctorId, List<DoctorStateSpeciality> listOfDoctorStateSpecialities){
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(doctorId);

        int rows = doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(doctorId);

        for(DoctorStateSpeciality eachStateSpeciality : listOfDoctorStateSpecialities){
            for(String eachSpeciality : eachStateSpeciality.getSpecialityList()){
                doctorSpecialityMappingRepository.insertIntoDoctorSpecialityMapping
                        (doctorId, eachSpeciality, eachStateSpeciality.getStateCode());
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
                doctorMaster.getDoctorGender(), doctorMaster.getIsActive(), doctorStateSpecialityList);
    }

    public DoctorMaster getByEmail(String doctorEmail) {
        return doctorMasterRepository.getDoctorByEmail(doctorEmail);
    }

    public AddDoctorAvailabilityResponse addAvailability(AddDoctorAvailabilityRequest request) {

        for(AddDoctorAvailabilityList eachFacility : request.getFacilityList()){
            DoctorFacilityAvailability doctorFacilityAvailability = new DoctorFacilityAvailability();
            doctorFacilityAvailability.setDfAvailabilityId(Utils.generateUUID());
            doctorFacilityAvailability.setDoctorMasterId(request.getDoctorId());
            doctorFacilityAvailability.setFacilityMasterId(eachFacility.getFacilityId());
            doctorFacilityAvailability.setAvailableDate(eachFacility.getAvailableDate());
            doctorFacilityAvailability.setAvailableStartTime(eachFacility.getAvailableStartTime());
            doctorFacilityAvailability.setAvailableEndTime(eachFacility.getAvailableEndTime());
            doctorFacilityAvailabilityRepository.save(doctorFacilityAvailability);
        }
        return new AddDoctorAvailabilityResponse(request.getDoctorId(), request.getFacilityList());
    }

    public List<DoctorFacilities> getFacilitiesForDoctor(Integer doctorId) {
        return facilityMasterRepository.getFacilitiesForDoctor(doctorId);
    }

}
