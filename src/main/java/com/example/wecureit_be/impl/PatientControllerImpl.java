package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.*;
import com.example.wecureit_be.repository.*;
import com.example.wecureit_be.request.*;
import com.example.wecureit_be.response.BookAppointmentResponse;
import com.example.wecureit_be.response.FetchDatesResponse;
import com.example.wecureit_be.response.PatientBookingL1Response;
import com.example.wecureit_be.response.TimeSlot;
import com.example.wecureit_be.utilities.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
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

    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    private static final int defaultSlotDuration = 15;


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

    public PatientBookingL1Response appointmentBookingL1(PatientBookingRequest patientBookingRequest) {

        // todo saurabh - how to incorporate fac and doc's isActive flags

        //Filtering only by doctorId
        if(!ObjectUtils.isEmpty(patientBookingRequest.getDoctorMasterId()) &&
                ObjectUtils.isEmpty(patientBookingRequest.getSpecialityMasterId()) &&
                ObjectUtils.isEmpty(patientBookingRequest.getFacilityMasterId())){

            //fetch facilities
            List<DoctorFacilityAvailability> doctorFacilityAvailabilities =
                    doctorFacilityAvailabilityRepository.getAvailableFacilityById(patientBookingRequest.getDoctorMasterId());

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
        if(!ObjectUtils.isEmpty(patientBookingRequest.getSpecialityMasterId()) &&
                ObjectUtils.isEmpty(patientBookingRequest.getDoctorMasterId()) &&
                ObjectUtils.isEmpty(patientBookingRequest.getFacilityMasterId())){

            //fetch docs
            List<DoctorMaster> doctorMasterList = new ArrayList<>();
            List<FacilityMaster> facilityMasterList = new ArrayList<>();

            List<PractisingSpeciality> allPractisingSpecialities =
                    practisingSpecialityRepository.getDfaIdBySpecialty(patientBookingRequest.getSpecialityMasterId());

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
        if(!ObjectUtils.isEmpty(patientBookingRequest.getFacilityMasterId()) &&
                ObjectUtils.isEmpty(patientBookingRequest.getSpecialityMasterId()) &&
                ObjectUtils.isEmpty(patientBookingRequest.getDoctorMasterId())){

            //Fetch docs
            List<DoctorFacilityAvailability> availableDocs =
                    doctorFacilityAvailabilityRepository.getAvailableDoctorsById(patientBookingRequest.getFacilityMasterId());

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


    public PatientBookingL1Response appointmentBookingL2(PatientBookingRequest patientBookingRequest) {

        //Filtering by doctorId and facility
        if(!ObjectUtils.isEmpty(patientBookingRequest.getDoctorMasterId()) &&
                !ObjectUtils.isEmpty(patientBookingRequest.getFacilityMasterId()) &&
                    ObjectUtils.isEmpty(patientBookingRequest.getSpecialityMasterId())){

            List<DoctorFacilityAvailability> availableBookings =
                    doctorFacilityAvailabilityRepository.getAvailabilityByDocIdAndFacId
                            (patientBookingRequest.getDoctorMasterId(), patientBookingRequest.getFacilityMasterId());

            List<SpecialityMaster> specialityMasterList = new ArrayList<>();

            for(DoctorFacilityAvailability eachAvailability: availableBookings){
                List<PractisingSpeciality> practisingSpecialityList =
                        practisingSpecialityRepository.getSpecialitiesByDfaId(eachAvailability.getDfAvailabilityId());

                for(PractisingSpeciality eachSpeciality: practisingSpecialityList){
                    if(!specialityMasterList.contains(eachSpeciality.getSpecialityMaster())){
                        specialityMasterList.add(eachSpeciality.getSpecialityMaster());
                    }
                }
            }

            return new PatientBookingL1Response(null, specialityMasterList, null);
        }

        //Filtering by facility and speciality
        if(!ObjectUtils.isEmpty(patientBookingRequest.getFacilityMasterId()) &&
                !ObjectUtils.isEmpty(patientBookingRequest.getSpecialityMasterId()) &&
                    ObjectUtils.isEmpty(patientBookingRequest.getDoctorMasterId())){

            List<DoctorFacilityAvailability> availableBookings =
                    doctorFacilityAvailabilityRepository.getAvailabilityByFacIdAndSpecId
                            (patientBookingRequest.getFacilityMasterId(), patientBookingRequest.getSpecialityMasterId());

            List<DoctorMaster> doctorMasterList = new ArrayList<>();

            for(DoctorFacilityAvailability eachAvailability: availableBookings){
                if(!doctorMasterList.contains(eachAvailability.getDoctorMaster())){
                    doctorMasterList.add(eachAvailability.getDoctorMaster());
                }
            }

            return new PatientBookingL1Response(null, null, doctorMasterList);
        }

        //Filtering by speciality and doctorId
        if(!ObjectUtils.isEmpty(patientBookingRequest.getSpecialityMasterId()) &&
                !ObjectUtils.isEmpty(patientBookingRequest.getDoctorMasterId()) &&
                    ObjectUtils.isEmpty(patientBookingRequest.getFacilityMasterId())){

            List<DoctorFacilityAvailability> availableBookings =
                    doctorFacilityAvailabilityRepository.getAvailabilityByDocIdAndSpecId
                            (patientBookingRequest.getDoctorMasterId(), patientBookingRequest.getSpecialityMasterId());

            List<FacilityMaster> facilityMasterList = new ArrayList<>();

            for(DoctorFacilityAvailability eachAvailability: availableBookings){
                if(!facilityMasterList.contains(eachAvailability.getFacilityMaster())){
                    facilityMasterList.add(eachAvailability.getFacilityMaster());
                }
            }

            return new PatientBookingL1Response(facilityMasterList, null, null);
        }

        return null;
    }

    public List<FetchDatesResponse> appointmentDates (PatientBookingRequest patientBookingRequest) {

        List<DoctorFacilityAvailability> availableFacDocs =
                doctorFacilityAvailabilityRepository.getAvailabilityByDocIdAndSpecIdAndFacId
                        (patientBookingRequest.getDoctorMasterId(), patientBookingRequest.getSpecialityMasterId(),
                                patientBookingRequest.getFacilityMasterId());

        List<FetchDatesResponse> response = new ArrayList<>();

        for(DoctorFacilityAvailability each : availableFacDocs){
            response.add(new FetchDatesResponse(each.getDfAvailabilityId(), each.getAvailableDate(),
                    each.getIsFilled()));
        }
        return response;
    }

    public List<TimeSlot> findSlots (GetSlotsRequest getSlotsRequest) {

        DoctorFacilityAvailability doctorFacilityAvailability =
                doctorFacilityAvailabilityRepository.getByDfAvailabilityId(getSlotsRequest.getDfAvailabilityId());

        List<Appointments> docAppointments =
                appointmentsRepository.getAppointmentByDocId(doctorFacilityAvailability.getDoctorMaster().getDoctorMasterId());

        LocalTime currStartTime = doctorFacilityAvailability.getAvailableStartTime();
        int durationReq = getSlotsRequest.getDuration();
        List<TimeSlot> validSlots = new ArrayList<>();

        while (currStartTime.plusMinutes(durationReq).isBefore(doctorFacilityAvailability.getAvailableEndTime()) ||
                currStartTime.plusMinutes(durationReq).equals(doctorFacilityAvailability.getAvailableEndTime())) {

            LocalTime proposedStart = currStartTime;
            LocalTime proposedEnd = currStartTime.plusMinutes(durationReq);

            if (isSlotFree(proposedStart, proposedEnd, docAppointments)) {
                if (isLessThan60(proposedStart, proposedEnd, docAppointments, durationReq)) {
                    validSlots.add(new TimeSlot(proposedStart, proposedEnd));
                }
            }
            currStartTime = currStartTime.plusMinutes(defaultSlotDuration);
        }

        return validSlots;
    }

    private boolean isSlotFree(LocalTime start, LocalTime end, List<Appointments> appointments) {
        for (Appointments eachAppointment : appointments) {

            if (start.isBefore(eachAppointment.getEndTime())
                    && end.isAfter(eachAppointment.getStartTime())) {
                return false;
            }
        }
        return true;
    }

    private boolean isLessThan60(LocalTime start, LocalTime end, List<Appointments> appointments, int durationReq) {

        int workBefore = getContinuousWorkMinutes(start, appointments, false);
        int workAfter = getContinuousWorkMinutes(end, appointments, true);
        int totalContinuousWork = workBefore + durationReq + workAfter;

        return totalContinuousWork <= 60;
    }

    private int getContinuousWorkMinutes(LocalTime startTime, List<Appointments> appointments, boolean lookForward) {
        int totalTime = 0;
        LocalTime currentSearchPoint = startTime;
        boolean foundConnection = true;

        while (foundConnection) {
            foundConnection = false;

            for (Appointments eachAppointment : appointments) {
                if (lookForward) {
                    if (eachAppointment.getStartTime().equals(currentSearchPoint)) {
                        totalTime += eachAppointment.getDuration();
                        currentSearchPoint = eachAppointment.getEndTime();
                        foundConnection = true;
                        break;
                    }
                }
                else {
                    if (eachAppointment.getEndTime().equals(currentSearchPoint)) {
                        totalTime += eachAppointment.getDuration();
                        currentSearchPoint = eachAppointment.getStartTime();
                        foundConnection = true;
                        break;
                    }
                }
            }
        }
        return totalTime;
    }

    public BookAppointmentResponse bookAppointment(BookAppointmentRequest bookAppointmentRequest) {
        PatientMaster patientMaster =
                patientMasterRepository.getPatientById(bookAppointmentRequest.getPatientMasterId());
        SpecialityMaster specialityMaster =
                specialityMasterRepository.getSpecialityById(bookAppointmentRequest.getSpecialityMasterId());
        DoctorFacilityAvailability doctorFacilityAvailability =
                doctorFacilityAvailabilityRepository.getByDfAvailabilityId(bookAppointmentRequest.getDfAvailabilityId());
        BookAppointmentResponse response = new BookAppointmentResponse();

        Appointments appointments = new Appointments();
        appointments.setAppointmentId(Utils.generateFiveDigitNumber());
        appointments.setDate(bookAppointmentRequest.getDate());
        appointments.setDuration(bookAppointmentRequest.getDuration());
        appointments.setPatientMaster(patientMaster);
        appointments.setDoctorFacilityAvailability(doctorFacilityAvailability);
        appointments.setStartTime(bookAppointmentRequest.getStartTime());
        appointments.setEndTime(bookAppointmentRequest.getEndTime());
        appointments.setSpecialityMaster(specialityMaster);
        appointmentsRepository.save(appointments);
        BeanUtils.copyProperties(appointments, response);
        return response;
    }

}
