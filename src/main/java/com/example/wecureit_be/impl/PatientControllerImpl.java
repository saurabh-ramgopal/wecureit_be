package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.*;
import com.example.wecureit_be.repository.*;
import com.example.wecureit_be.request.PatientBookingRequest;
import com.example.wecureit_be.request.PatientRegistrationRequest;
import com.example.wecureit_be.response.PatientBookingL1Response;
import com.example.wecureit_be.utilities.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import com.example.wecureit_be.request.PatientUpdateRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.util.*;

@Slf4j
@Service
public class PatientControllerImpl {

    @Autowired
    PatientMasterRepository patientMasterRepository;

    @Autowired
    DoctorFacilityAvailabilityRepository doctorFacilityAvailabilityRepository;

    @Autowired
    PractisingSpecialityRepository practisingSpecialityRepository;


    public PatientMaster addOrUpdate(PatientMaster patientMaster) {
        // Only allow updating email and phone for an existing patient.
        if (patientMaster == null || patientMaster.getPatientMasterId() == null) {
            throw new IllegalArgumentException("patientMaster and patientMaster.patientMasterId must be provided for update");
        }

        PatientMaster existing = patientMasterRepository.getPatientById(patientMaster.getPatientMasterId());
        if (existing == null) {
            throw new IllegalArgumentException("Patient with id " + patientMaster.getPatientMasterId() + " does not exist");
        }

        // Only update allowed fields
        if (patientMaster.getPatientEmail() != null) {
            existing.setPatientEmail(patientMaster.getPatientEmail());
        }
        if (patientMaster.getPatientPhone() != null) {
            existing.setPatientPhone(patientMaster.getPatientPhone());
        }

        return patientMasterRepository.save(existing);
    }

    public PatientMaster getById(Integer patientId) {
        return patientMasterRepository.getPatientById(patientId);
    }

    public PatientMaster getByEmail(String patientEmail) {
        return patientMasterRepository.getPatientByEmail(patientEmail);
    }

    @SneakyThrows
    public PatientMaster newRegistration(PatientRegistrationRequest patientRegistrationRequest){
        log.info("adding new patient details:{}", patientRegistrationRequest.getName());
        PatientMaster patientMaster = new PatientMaster();
        patientMaster.setPatientMasterId(Utils.generateFiveDigitNumber()); //to-do change accordingly when auto-increment implemented
//        patientMaster.setPatientMasterId(Utils.generateUUID()); to be added if we are using String as PK
        patientMaster.setPatientName(patientRegistrationRequest.getName());
        patientMaster.setPatientEmail(patientRegistrationRequest.getEmail());
        patientMaster.setPatientDob(patientRegistrationRequest.getDob());
        patientMaster.setPatientGender(patientRegistrationRequest.getGender());
        patientMaster.setPatientPhone(patientRegistrationRequest.getPhone());
        patientMaster = patientMasterRepository.save(patientMaster);

        Map<String, Object> claims = new HashMap<>();
        claims.put("patientMasterId", patientMaster.getPatientMasterId());
        claims.put("role", "patient");
        FirebaseAuth.getInstance().setCustomUserClaims(patientRegistrationRequest.getFirebaseUid(), claims);

        System.out.println("Claims updated for user: " + patientRegistrationRequest.getFirebaseUid());
        return patientMaster;

    }

    public PatientMaster updatePatient(Integer patientId, PatientUpdateRequest updateRequest) {
        if (patientId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "patientId is required");
        }
        PatientMaster existing = patientMasterRepository.getPatientById(patientId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        }

        if (updateRequest.getEmail() != null) {
            existing.setPatientEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhone() != null) {
            existing.setPatientPhone(updateRequest.getPhone());
        }

        return patientMasterRepository.save(existing);
    }

    public PatientBookingL1Response appointmentBookingL1(PatientBookingRequest patientFilterRequest) {

        // todo saurabh - how to incorporate fac and doc's isActive flags

        //Filtering only by doctorId
        if(!ObjectUtils.isEmpty(patientFilterRequest.getDoctorMasterId()) &&
                ObjectUtils.isEmpty(patientFilterRequest.getSpecialityMasterId()) &&
                ObjectUtils.isEmpty(patientFilterRequest.getFacilityMasterId())){

            //fetch facilities
            List<DoctorFacilityAvailability> doctorFacilityAvailabilities =
                    doctorFacilityAvailabilityRepository.getAvailableFacilityById(patientFilterRequest.getDoctorMasterId());

            List<FacilityMaster> facilityMasterList = new ArrayList<>();
            for(DoctorFacilityAvailability each: doctorFacilityAvailabilities){
                if(!facilityMasterList.contains(each.getFacilityMaster())){
                    facilityMasterList.add(each.getFacilityMaster());
                }
            }

            //fetch specs
            List<SpecialityMaster> specialityMasterList = new ArrayList<>();

            for(DoctorFacilityAvailability eachDocFacAvailability: doctorFacilityAvailabilities){
                List<PractisingSpeciality> practisingSpecialityList =
                        practisingSpecialityRepository.getSpecialitiesByDfaId(eachDocFacAvailability.getDfAvailabilityId());

                for(PractisingSpeciality eachSpeciality: practisingSpecialityList){
                    if(!specialityMasterList.contains(eachSpeciality.getSpecialityMaster())){
                        specialityMasterList.add(eachSpeciality.getSpecialityMaster());
                    }

                }
            }

            //res (facility + speciality)
            return new PatientBookingL1Response(facilityMasterList, specialityMasterList, null);
        }

        //Filtering only by specialityId
        if(!ObjectUtils.isEmpty(patientFilterRequest.getSpecialityMasterId()) &&
                ObjectUtils.isEmpty(patientFilterRequest.getDoctorMasterId()) &&
                ObjectUtils.isEmpty(patientFilterRequest.getFacilityMasterId())){

            //fetch docs
            List<DoctorMaster> doctorMasterList = new ArrayList<>();
            List<FacilityMaster> facilityMasterList = new ArrayList<>();

            List<PractisingSpeciality> allPractisingSpecialities =
                    practisingSpecialityRepository.getDfaIdBySpecialty(patientFilterRequest.getSpecialityMasterId());

            for(PractisingSpeciality each: allPractisingSpecialities){
                if(!doctorMasterList.contains(each.getDoctorFacilityAvailability().getDoctorMaster())){
                    doctorMasterList.add(each.getDoctorFacilityAvailability().getDoctorMaster());
                }
                if(!facilityMasterList.contains(each.getDoctorFacilityAvailability().getFacilityMaster())){
                    facilityMasterList.add(each.getDoctorFacilityAvailability().getFacilityMaster());
                }
            }

            //res (doctor + facility)
            return new PatientBookingL1Response(facilityMasterList, null, doctorMasterList);
        }

        //Filtering only by facilityId
        if(!ObjectUtils.isEmpty(patientFilterRequest.getFacilityMasterId()) &&
                ObjectUtils.isEmpty(patientFilterRequest.getSpecialityMasterId()) &&
                ObjectUtils.isEmpty(patientFilterRequest.getDoctorMasterId())){

            //Fetch docs
            List<DoctorFacilityAvailability> availableDocs =
                    doctorFacilityAvailabilityRepository.getAvailableDoctorsById(patientFilterRequest.getFacilityMasterId());

            List<DoctorMaster> doctorMasterList = new ArrayList<>();
            for(DoctorFacilityAvailability each: availableDocs){
                if(!doctorMasterList.contains(each.getDoctorMaster())){
                    doctorMasterList.add(each.getDoctorMaster());
                }
            }

            //fetch speciality
            List<SpecialityMaster> specialityMasterList = new ArrayList<>();
            for(DoctorFacilityAvailability each: availableDocs){
                List<PractisingSpeciality> listOfSpeciality =
                        practisingSpecialityRepository.getSpecialitiesByDfaId(each.getDfAvailabilityId());

                for(PractisingSpeciality eachSpec: listOfSpeciality){
                    if(!specialityMasterList.contains(eachSpec.getSpecialityMaster())){
                        specialityMasterList.add(eachSpec.getSpecialityMaster());
                    }
                }
            }

            //res (doctor + speciality)
            return new PatientBookingL1Response(null, specialityMasterList, doctorMasterList);
        }

        return null;
    }


}
