package com.example.wecureit_be.controller;

import com.example.wecureit_be.impl.DoctorControllerImpl;
import com.example.wecureit_be.request.AddAppointmentNoteRequest;
import com.example.wecureit_be.request.AddDoctorAvailabilityRequest;
import com.example.wecureit_be.request.DeleteDoctorAvailabilityRequest;
import com.example.wecureit_be.request.EditDoctorAvailabilityRequest;
import com.example.wecureit_be.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @GetMapping(value="/getById")
    public DoctorDetails getById(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getById(doctorId);
    }

    @PostMapping(value = "/availability/add")
    public AddDoctorAvailabilityResponse addAvailability(@RequestBody AddDoctorAvailabilityRequest addDoctorAvailabilityRequest) {
        return doctorControllerImpl.addAvailability(addDoctorAvailabilityRequest);
    }

    @PostMapping(value = "/availability/edit")
    public EditDoctorAvailabilityResponse editAvailability(@RequestBody EditDoctorAvailabilityRequest editDoctorAvailabilityRequest) {
        return doctorControllerImpl.editAvailability(editDoctorAvailabilityRequest);
    }

    @PostMapping(value = "/availability/delete")
    public DeleteDoctorAvailabilityResponse editAvailability(@RequestBody DeleteDoctorAvailabilityRequest deleteDoctorAvailabilityRequest) {
        return doctorControllerImpl.deleteAvailability(deleteDoctorAvailabilityRequest);
    }

    @GetMapping(value = "/availability/getSummary")
    public AddDoctorAvailabilityResponse getSummary(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getSummary(doctorId);
    }

    @GetMapping(value="facilities/getById")
    public List<FacilityDetails> getFacilitiesForDoctor(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getFacilitiesForDoctor(doctorId);
    }

    @PostMapping(value="appointments/addNote")
    public Boolean addAppointmentNote (@RequestBody AddAppointmentNoteRequest addAppointmentNoteRequest) {
        return doctorControllerImpl.addAppointmentNote(addAppointmentNoteRequest);
    }

    @GetMapping(value="appointments/getNextTwoWeeks")
    public List<BookAppointmentResponse> getFutureAppointments(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getFutureAppointments(doctorId);
    }

    @GetMapping(value="appointments/getAllPast")
    public List<BookAppointmentResponse> getPastAppointments(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getPastAppointments(doctorId);
    }

}
