package com.example.wecureit_be.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.example.wecureit_be.utilities.Utils;

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
        // Validation: ensure at least one license (state + speciality) is provided
        boolean hasValidLicense = false;
        if (addDoctorRequest.getDoctorStateSpeciality() != null) {
            for (com.example.wecureit_be.request.DoctorStateSpeciality ds : addDoctorRequest.getDoctorStateSpeciality()) {
                if (ds != null && ds.getStateCode() != null && !ds.getStateCode().trim().isEmpty()
                        && ds.getSpecialityList() != null && !ds.getSpecialityList().isEmpty()) {
                    hasValidLicense = true;
                    break;
                }
            }
        }
        // Fallback: if frontend sent only flat specialityList, treat as invalid because state is required
        if (!hasValidLicense) {
            throw new IllegalArgumentException("At least one state license with at least one speciality is required.");
        }
        Integer incomingId = addDoctorRequest.getDoctorMasterId();
        DoctorMaster doctorMaster;

        if (incomingId != null) {
            // Update existing doctor
            doctorMaster = doctorMasterRepository.getDoctorById(incomingId);
            if (doctorMaster == null) {
                // If not present, create new with provided id
                doctorMaster = new DoctorMaster();
                doctorMaster.setDoctorMasterId(incomingId);
            }
        } else {
            // Create new doctor
            doctorMaster = new DoctorMaster();
            doctorMaster.setDoctorMasterId(Utils.generateFiveDigitNumber());
        }

        // Update fields if provided
        if (addDoctorRequest.getDoctorName() != null) doctorMaster.setDoctorName(addDoctorRequest.getDoctorName());
        if (addDoctorRequest.getDoctorGender() != null) doctorMaster.setDoctorGender(addDoctorRequest.getDoctorGender());
        if (addDoctorRequest.getDoctorPassword() != null) doctorMaster.setDoctorPassword(addDoctorRequest.getDoctorPassword());
        if (addDoctorRequest.getDoctorEmail() != null) doctorMaster.setDoctorEmail(addDoctorRequest.getDoctorEmail());
        if (doctorMaster.getIsActive() == null) doctorMaster.setIsActive(true);

        doctorMasterRepository.save(doctorMaster);

        // If frontend provided a state-grouped structure, use it to persist mappings with state
        if (addDoctorRequest.getDoctorStateSpeciality() != null && !addDoctorRequest.getDoctorStateSpeciality().isEmpty()) {
            // delete existing mappings
            Integer docId = doctorMaster.getDoctorMasterId();
            doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(docId);
            // create new mappings using state info
            for (com.example.wecureit_be.request.DoctorStateSpeciality ds : addDoctorRequest.getDoctorStateSpeciality()) {
                StateMaster stateMaster = null;
                if (ds.getStateCode() != null && !ds.getStateCode().isEmpty()) {
                    stateMaster = stateMasterRepository.getStateById(ds.getStateCode());
                }
                for (String specId : ds.getSpecialityList()) {
                    SpecialityMaster specialityMaster = specialityMasterRepository.getSpecialityById(specId);
                    if (specialityMaster != null) {
                        DoctorSpecialityMapping mapping = new DoctorSpecialityMapping();
                        mapping.setDoctorMaster(doctorMaster);
                        mapping.setSpecialityMaster(specialityMaster);
                        mapping.setStateMaster(stateMaster);
                        doctorSpecialityMappingRepository.save(mapping);
                    }
                }
            }
            List<DoctorSpecialityMapping> listOfSpeciality = doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorMaster.getDoctorMasterId());
            return prepareDocResponse(doctorMaster, listOfSpeciality);
        }

        // Fallback: Frontend sends a flat list of speciality IDs (specialityList).
        // For update, remove existing mappings first so we don't create duplicates.
        Integer docId = doctorMaster.getDoctorMasterId();
        doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(docId);

        List<String> specList = addDoctorRequest.getSpecialityList();
        if (specList != null) {
            for (String specId : specList) {
                SpecialityMaster specialityMaster = specialityMasterRepository.getSpecialityById(specId);
                if (specialityMaster != null) {
                    DoctorSpecialityMapping mapping = new DoctorSpecialityMapping();
                    mapping.setDoctorMaster(doctorMaster);
                    mapping.setSpecialityMaster(specialityMaster);
                    // stateMaster intentionally left null because frontend provided a flat list
                    mapping.setStateMaster(null);
                    doctorSpecialityMappingRepository.save(mapping);
                }
            }
        }

        // Return prepared response (flat speciality list) to match frontend expectations
        List<DoctorSpecialityMapping> listOfSpeciality = doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(docId);
        return prepareDocResponse(doctorMaster, listOfSpeciality);

    }

    public DoctorDetails updateDoctorStateSpecialities(Integer doctorId, List<DoctorStateSpeciality> listOfDoctorStateSpecialities){
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(doctorId);

    doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(doctorId);

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
        Integer doctorId = deleteDoctorRequest.getDoctorMasterId();
        Boolean isActive = deleteDoctorRequest.getIsActive();

        if (doctorId == null) return null;

        // If frontend indicates isActive == false, perform a hard delete (remove mappings then doctor)
        if (Boolean.FALSE.equals(isActive)) {
            // delete speciality mappings first to avoid FK constraint violations
            doctorSpecialityMappingRepository.deleteDoctorAllSpeciality(doctorId);
            // delete doctor record
            try {
                doctorMasterRepository.deleteById(doctorId);
            } catch (Exception ex) {
                // fall back to soft-delete if hard delete fails
                DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(doctorId);
                if (doctorMaster != null) {
                    doctorMaster.setIsActive(false);
                    return doctorMasterRepository.save(doctorMaster);
                }
                return null;
            }
            return null;
        }

        // Default behavior: update isActive flag (soft delete / restore)
        DoctorMaster doctorMaster = doctorMasterRepository.getDoctorById(doctorId);
        if (doctorMaster == null) return null;
        doctorMaster.setIsActive(isActive);
        return doctorMasterRepository.save(doctorMaster);
    }

    public DoctorDetails getById(Integer doctorId) {
        DoctorMaster doctorMaster =  doctorMasterRepository.getDoctorById(doctorId);

        List<DoctorSpecialityMapping> listOfSpeciality =
                doctorSpecialityMappingRepository.getDoctorSpecialityByDoctorId(doctorId);
        return prepareDocResponse(doctorMaster, listOfSpeciality);
    }


    /**
     * Prepare a DoctorDetails response expected by the frontend.
     * The frontend expects a flat list of specialities and also reads doctorPassword.
     */
    public DoctorDetails prepareDocResponse (DoctorMaster doctorMaster, List<DoctorSpecialityMapping> list){

    // Build a flat list of SpecialityMaster objects from mappings
    List<SpecialityMaster> specialityMasters = list.stream()
        .map(DoctorSpecialityMapping::getSpecialityMaster)
        .filter(s -> s != null)
        .collect(Collectors.toList());

    // Build licenses grouped by state (stateCode -> list of speciality ids)
    Map<String, List<String>> stateToSpecIds = list.stream()
        .filter(m -> m.getSpecialityMaster() != null)
        .collect(Collectors.groupingBy(
            m -> m.getStateMaster() == null ? "" : m.getStateMaster().getStateCode(),
            Collectors.mapping(m -> m.getSpecialityMaster().getSpecialityMasterId(), Collectors.toList())
        ));

    List<com.example.wecureit_be.response.DoctorLicense> licenses = stateToSpecIds.entrySet().stream()
        .map(e -> new com.example.wecureit_be.response.DoctorLicense(e.getKey(), e.getValue()))
        .collect(Collectors.toList());

    // Create and return DoctorDetails with password, flat speciality list and licenses
    return new DoctorDetails(
        doctorMaster.getDoctorMasterId(),
        doctorMaster.getDoctorName(),
        doctorMaster.getDoctorEmail(),
        doctorMaster.getDoctorPassword(),
        doctorMaster.getDoctorGender(),
        specialityMasters,
        licenses
    );
    }

    public DoctorMaster getByEmail(String doctorEmail) {
        return doctorMasterRepository.getDoctorByEmail(doctorEmail);
    }
}
