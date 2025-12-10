package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.impl.PatientControllerImpl;
import com.example.wecureit_be.request.*;
import com.example.wecureit_be.response.BookAppointmentResponse;
import com.example.wecureit_be.response.FetchDatesResponse;
import com.example.wecureit_be.response.PatientAppointments;
import com.example.wecureit_be.response.PatientBookingL1Response;
import com.example.wecureit_be.response.TimeSlot;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/patient")
public class PatientController {

    //private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    PatientControllerImpl patientControllerImpl;

    // @Deprecated
    @PostMapping(value="/addOrUpdate")
    public PatientMaster addOrUpdate (@RequestBody PatientMaster patientMaster){
        //log.warn("Deprecated endpoint /patient/addOrUpdate called - prefer PATCH /patient/{id}");
        return patientControllerImpl.addOrUpdate(patientMaster);
    }

    @PatchMapping(value="/{patientId}")
    public PatientMaster patchUpdate(@PathVariable Integer patientId, @RequestBody @Valid PatientUpdateRequest updateRequest){
        return patientControllerImpl.updatePatient(patientId, updateRequest);
    }

    @GetMapping(value="/getById")
    public PatientMaster getById (@RequestParam Integer patientId){
        return patientControllerImpl.getById(patientId);
    }

    @PostMapping(value="/registration")
    public PatientMaster newRegistration (@RequestBody PatientRegistrationRequest patientRegistrationRequest) {
        return patientControllerImpl.newRegistration(patientRegistrationRequest);
    }

    @PostMapping(value="/bookAppointment/fetchL1")
    public PatientBookingL1Response appointmentBookingL1(@RequestBody PatientBookingRequest patientBookingRequest){
        return patientControllerImpl.appointmentBookingL1(patientBookingRequest);
    }

    @PostMapping(value="/bookAppointment/fetchL2")
    public PatientBookingL1Response appointmentBookingL2(@RequestBody PatientBookingRequest patientBookingRequest){
        return patientControllerImpl.appointmentBookingL2(patientBookingRequest);
    }

    @PostMapping(value="/bookAppointment/fetchDates")
    public List<FetchDatesResponse> appointmentDates (@RequestBody PatientBookingRequest patientBookingRequest){
        return patientControllerImpl.appointmentDates(patientBookingRequest);
    }

    @PostMapping(value="/bookAppointment/fetchSlots")
    public List<TimeSlot> findSlots (@RequestBody GetSlotsRequest getSlotsRequest){
        return patientControllerImpl.findSlots(getSlotsRequest);
    }

    @PostMapping(value="/bookAppointment")
    public BookAppointmentResponse bookAppointment (@RequestBody BookAppointmentRequest bookAppointmentRequest){
        return patientControllerImpl.bookAppointment(bookAppointmentRequest);
    }

    // Upcoming appointments for a patient
    @GetMapping(value="/upcomingAppointments")
    public List<PatientAppointments> getUpcomingAppointments(@RequestParam Integer patientId) {
        return patientControllerImpl.getUpcomingAppointments(patientId);
    }
    
    // Old Appointments for a patient
    @GetMapping(value="/oldAppointments")
    public List<PatientAppointments> getOldAppointments(@RequestParam Integer patientId) {
        return patientControllerImpl.getOldAppointments(patientId);
    }

    @GetMapping(value="/cancelledAppointments")
    public List<PatientAppointments> getAllCancelledAppointments(@RequestParam Integer patientId) {
        return patientControllerImpl.getAllCancelledAppointments(patientId);
    }

}
