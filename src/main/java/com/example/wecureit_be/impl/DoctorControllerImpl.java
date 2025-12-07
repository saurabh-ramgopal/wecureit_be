package com.example.wecureit_be.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.example.wecureit_be.entity.*;
import com.example.wecureit_be.request.*;
import com.example.wecureit_be.response.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wecureit_be.repository.*;
import com.example.wecureit_be.utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import lombok.SneakyThrows;

@Service
public class DoctorControllerImpl {

    @Autowired
    FacilityMasterRepository facilityMasterRepository;

    @Autowired
    DoctorMasterRepository doctorMasterRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    @Autowired
    DoctorSpecialityMappingRepository doctorSpecialityMappingRepository;

    @Autowired
    DoctorFacilityAvailabilityRepository doctorFacilityAvailabilityRepository;

    @Autowired
    PractisingSpecialityRepository practisingSpecialityRepository;

    @Autowired
    FacilitySpecialityMappingRepository facilitySpecialityMappingRepository;

    @Autowired
    AppointmentsRepository appointmentsRepository;

    public List<DoctorDetails> getAllDoctors(){
        List<DoctorMaster> doctorMasterList = doctorMasterRepository.findAll();
        List<DoctorDetails> listOfDoctorDetails = new ArrayList<>();
        for(DoctorMaster doctorMaster:doctorMasterList){
            if(doctorMaster.getIsActive()){
                List<DoctorSpecialityMapping> listOfSpeciality =
                        doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorMaster.getDoctorMasterId());

                List<Appointments> anyFutureAppointments =
                        appointmentsRepository.getAnyFutureAppointmentsByDoctor(
                                doctorMaster.getDoctorMasterId(), LocalDate.now());

                boolean isDeletable = anyFutureAppointments.isEmpty();

                listOfDoctorDetails.add(prepareDocResponse(doctorMaster, listOfSpeciality, isDeletable));
            }
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

        return prepareDocResponse(doctorMaster, listOfSpeciality, null);
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
        return prepareDocResponse(doctorMaster, listOfSpeciality, null);
    }


    public DoctorDetails prepareDocResponse (DoctorMaster doctorMaster, List<DoctorSpecialityMapping> list, Boolean isDeletable){

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
                doctorMaster.getDoctorGender(), doctorMaster.getIsActive(), isDeletable, doctorStateSpecialityList);
    }

    public DoctorMaster getByEmail(String doctorEmail) {
        return doctorMasterRepository.getDoctorByEmail(doctorEmail);
    }

    public AddDoctorAvailabilityResponse addAvailability(AddDoctorAvailabilityRequest request) {
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(request.getDoctorId());

        for(AddDoctorAvailabilityList eachFacility : request.getFacilityList()){
            FacilityMaster facilityMaster = facilityMasterRepository.getFacilityById(eachFacility.getFacilityId());

            DoctorFacilityAvailability doctorFacilityAvailability = new DoctorFacilityAvailability();
            doctorFacilityAvailability.setDfAvailabilityId(Utils.generateUUID());
            doctorFacilityAvailability.setDoctorMaster(doctorMaster);
            doctorFacilityAvailability.setFacilityMaster(facilityMaster);
            doctorFacilityAvailability.setAvailableDate(eachFacility.getAvailableDate());
            doctorFacilityAvailability.setAvailableStartTime(eachFacility.getAvailableStartTime());
            doctorFacilityAvailability.setAvailableEndTime(eachFacility.getAvailableEndTime());
            doctorFacilityAvailability.setIsActive(true);
            doctorFacilityAvailabilityRepository.save(doctorFacilityAvailability);

            List<SpecialityMaster> specialityMasterList;

            List<DoctorSpecialityMapping> docSpecialities =
                    doctorSpecialityMappingRepository.getSpecialityByDoctorIdAndStateCode
                            (doctorMaster.getDoctorMasterId(), facilityMaster.getStateCode().getStateCode());

            List<FacilitySpecialityMapping> facSpecialities =
                    facilitySpecialityMappingRepository.getSpecialityByFacilityId
                            (facilityMaster.getFacilityMasterId());

            // Extract speciality IDs from facility list
            Set<String> facilitySpecialityIds = facSpecialities.stream()
                    .map(f -> f.getSpecialityMaster().getSpecialityMasterId())
                    .collect(Collectors.toSet());

            // Filter doctor specialities that match facility specialities
            specialityMasterList = docSpecialities.stream()
                    .map(DoctorSpecialityMapping::getSpecialityMaster)
                    .filter(s -> facilitySpecialityIds.contains(s.getSpecialityMasterId()))
                    .distinct()
                    .collect(Collectors.toList());


            for(SpecialityMaster each : specialityMasterList){
                practisingSpecialityRepository.insertIntoPractisingSpeciality
                        (doctorFacilityAvailability.getDfAvailabilityId(), each.getSpecialityMasterId());
            }
        }
        return new AddDoctorAvailabilityResponse(request.getDoctorId(), request.getFacilityList());
    }

    public List<FacilityDetails> getFacilitiesForDoctor(Integer doctorId) {
        List<DoctorFacilities> doctorEligibleFacilities = facilityMasterRepository.getFacilitiesForDoctor(doctorId);
        Map<String, List<String>> facilityMap = new HashMap<>();

        for(DoctorFacilities eachFacility : doctorEligibleFacilities){
            if(facilityMap.containsKey(eachFacility.getFacilityMasterId())){
                List<String> myList = facilityMap.get(eachFacility.getFacilityMasterId());
                myList.add(eachFacility.getSpecialityMasterId());
            }
            else{
                List<String> facilityTempList = new ArrayList<>();
                facilityTempList.add(eachFacility.getSpecialityMasterId());
                facilityMap.put(eachFacility.getFacilityMasterId(), facilityTempList);
            }
        }

        List<FacilityDetails> responseList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : facilityMap.entrySet()) {
            FacilityMaster facilityMaster = facilityMasterRepository.getFacilityById(entry.getKey());

            FacilityDetails facilityDetail = new FacilityDetails();

            List<SpecialityMaster> facilitySpecialitiesByDoc = new ArrayList<>();
            for(String specialityId : entry.getValue()){
                SpecialityMaster specialityMaster = specialityMasterRepository.getSpecialityById(specialityId);
                facilitySpecialitiesByDoc.add(specialityMaster);
            }

            BeanUtils.copyProperties(facilityMaster, facilityDetail);
            facilityDetail.setSpeciality(facilitySpecialitiesByDoc);
            facilityDetail.setStateCode(facilityMaster.getStateCode().getStateCode());
            facilityDetail.setStateName(facilityMaster.getStateCode().getStateName());
            responseList.add(facilityDetail);
        }

        return responseList;
    }

    public Boolean addAppointmentNote(AddAppointmentNoteRequest addAppointmentNoteRequest) {
        appointmentsRepository.updateAppointmentNote
                (addAppointmentNoteRequest.getAppointmentId(), addAppointmentNoteRequest.getAppointmentNote());

        return true;
    }

    public List<BookAppointmentResponse> getFutureAppointments (Integer doctorId) {

        List<BookAppointmentResponse> response = new ArrayList<>();

        List<Appointments> docAppointments =
                appointmentsRepository.getAppointmentForNext2Weeks(doctorId);

        for(Appointments eachAppointment : docAppointments){
            BookAppointmentResponse bookAppointmentResponse = new BookAppointmentResponse();
            BeanUtils.copyProperties(eachAppointment, bookAppointmentResponse);
            response.add(bookAppointmentResponse);
        }
        return response;
    }

    public List<BookAppointmentResponse> getPastAppointments(Integer doctorId) {
        List<BookAppointmentResponse> response = new ArrayList<>();

        List<Appointments> docAppointments =
                appointmentsRepository.getPastAppointments(doctorId);

        for(Appointments eachAppointment : docAppointments){
            BookAppointmentResponse bookAppointmentResponse = new BookAppointmentResponse();
            BeanUtils.copyProperties(eachAppointment, bookAppointmentResponse);
            response.add(bookAppointmentResponse);
        }
        return response;
    }

    public AddDoctorAvailabilityResponse getSummary(Integer doctorId) {

        List<DoctorFacilityAvailability> list =
                doctorFacilityAvailabilityRepository.getFutureAvailabilityByDocId(doctorId);

        List<AddDoctorAvailabilityList> resFacilityList = new ArrayList<>();
        List<SpecialityMaster> specialityMasterList = new ArrayList<>();

        for(DoctorFacilityAvailability eachAvailability : list){

            List<PractisingSpeciality> practisingSpeciality =
                    practisingSpecialityRepository.getSpecialitiesByDfaId(eachAvailability.getDfAvailabilityId());

            List<Appointments> appointmentsByDfAvailability = appointmentsRepository.
                    getAppointmentsByDfAvailabilityId(eachAvailability.getDfAvailabilityId());

            boolean isEditable = appointmentsByDfAvailability.isEmpty();

            for(PractisingSpeciality each: practisingSpeciality){
                if(!specialityMasterList.contains(each.getSpecialityMaster()))
                    specialityMasterList.add(each.getSpecialityMaster());
            }
            AddDoctorAvailabilityList docAvailability = new AddDoctorAvailabilityList();

            docAvailability.setDfAvailabilityId(eachAvailability.getDfAvailabilityId());
            docAvailability.setFacilityId(eachAvailability.getFacilityMaster().getFacilityMasterId());
            docAvailability.setFacilityName(eachAvailability.getFacilityMaster().getFacilityName());
            docAvailability.setFacilityStreet(eachAvailability.getFacilityMaster().getFacilityStreet());
            docAvailability.setSpeciality(specialityMasterList);
            docAvailability.setStateName(eachAvailability.getFacilityMaster().getStateCode().getStateName());
            docAvailability.setStateCode(eachAvailability.getFacilityMaster().getStateCode().getStateCode());
            docAvailability.setAvailableDate(eachAvailability.getAvailableDate());
            docAvailability.setAvailableStartTime(eachAvailability.getAvailableStartTime());
            docAvailability.setAvailableEndTime(eachAvailability.getAvailableEndTime());
            docAvailability.setEditable(isEditable);
            resFacilityList.add(docAvailability);
        }

        return new AddDoctorAvailabilityResponse(doctorId, resFacilityList);
    }

    public EditDoctorAvailabilityResponse editAvailability (EditDoctorAvailabilityRequest editDoctorAvailabilityRequest) {

        DoctorFacilityAvailability doctorFacilityAvailability = doctorFacilityAvailabilityRepository
                .getByDfAvailabilityId(editDoctorAvailabilityRequest.getDfAvailabilityId());

        doctorFacilityAvailability.setAvailableStartTime(editDoctorAvailabilityRequest.getAvailableStartTime());
        doctorFacilityAvailability.setAvailableEndTime(editDoctorAvailabilityRequest.getAvailableEndTime());
        doctorFacilityAvailabilityRepository.save(doctorFacilityAvailability);

        return new EditDoctorAvailabilityResponse(editDoctorAvailabilityRequest.getDfAvailabilityId(),
                doctorFacilityAvailability.getAvailableStartTime(), doctorFacilityAvailability.getAvailableEndTime());
    }

    public DeleteDoctorAvailabilityResponse deleteAvailability(DeleteDoctorAvailabilityRequest deleteDoctorAvailabilityRequest) {

        DoctorFacilityAvailability doctorFacilityAvailability = doctorFacilityAvailabilityRepository
                .getByDfAvailabilityId(deleteDoctorAvailabilityRequest.getDfAvailabilityId());

        doctorFacilityAvailability.setIsActive(false);
        doctorFacilityAvailabilityRepository.save(doctorFacilityAvailability);

        return new DeleteDoctorAvailabilityResponse(doctorFacilityAvailability.getDfAvailabilityId(),
                doctorFacilityAvailability.getIsActive());
    }
}
