package com.example.wecureit_be.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.example.wecureit_be.entity.FacilityMaster;
import com.example.wecureit_be.entity.FacilitySpecialityMapping;
import com.example.wecureit_be.entity.SpecialityMaster;
import com.example.wecureit_be.entity.StateMaster;
import com.example.wecureit_be.repository.FacilityMasterRepository;
import com.example.wecureit_be.repository.FacilitySpecialityMappingRepository;
import com.example.wecureit_be.repository.SpecialityMasterRepository;
import com.example.wecureit_be.repository.StateMasterRepository;
import com.example.wecureit_be.request.AddOrUpdateFacilityRequest;
import com.example.wecureit_be.request.DeleteFacilityRequest;
import com.example.wecureit_be.response.FacilityDetails;
import com.example.wecureit_be.response.RoomDetail;
import com.example.wecureit_be.utilities.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FacilityControllerImpl {

    @Autowired
    FacilityMasterRepository facilityMasterRepository;

    @Autowired
    SpecialityMasterRepository specialityMasterRepository;

    @Autowired
    FacilitySpecialityMappingRepository facilitySpecialityMappingRepository;

    @Autowired
    StateMasterRepository stateMasterRepository;


    public List<FacilityDetails> getAllFacility(){

        List<FacilityDetails> response = new ArrayList<>();
        List<FacilityMaster> listOfFacilityMaster = facilityMasterRepository.getAllFacility();

        for(FacilityMaster eachFacility : listOfFacilityMaster){
            FacilityDetails facilityDetail = new FacilityDetails();

            List<SpecialityMaster> specialityMaster = getSpecialityByFacilityId(eachFacility.getFacilityMasterId());

            BeanUtils.copyProperties(eachFacility, facilityDetail);
            facilityDetail.setSpeciality(specialityMaster);
            if (eachFacility.getStateCode() != null) {
                facilityDetail.setStateCode(eachFacility.getStateCode().getStateCode());
                facilityDetail.setStateName(eachFacility.getStateCode().getStateName());
            } else {
                facilityDetail.setStateCode(null);
                facilityDetail.setStateName(null);
            }
            response.add(facilityDetail);
        }
        return response;
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

        // Defensive handling: treat empty or blank stateCode as null and prefer
        // server-side resolution of stateCode/stateName. This preserves
        // backward-compatibility with older frontend payloads.
        String incomingStateCode = addOrUpdateFacilityRequest.getStateCode();
        log.info("addOrUpdateFacility called for facilityId={} incomingStateCode='{}'", addOrUpdateFacilityRequest.getFacilityMasterId(), incomingStateCode);
        StateMaster stateMaster = null;
        if (incomingStateCode != null && !incomingStateCode.trim().isEmpty()) {
            String codeCandidate = incomingStateCode.trim();
            // First try direct code lookup
            stateMaster = stateMasterRepository.getStateById(codeCandidate);
            log.info("Resolved stateMaster for code='{}' ? {}", codeCandidate, (stateMaster != null));

            // If not found, attempt to parse incomingStateCode in case FE sent a JSON object as a string
            if (stateMaster == null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> parsed = mapper.readValue(codeCandidate, java.util.Map.class);
                    Object parsedCode = parsed.get("stateCode");
                    if (parsedCode != null) {
                        String parsedCodeStr = String.valueOf(parsedCode).trim();
                        stateMaster = stateMasterRepository.getStateById(parsedCodeStr);
                        log.info("Parsed incomingStateCode JSON and looked up stateCode='{}' ? {}", parsedCodeStr, (stateMaster != null));
                    }
                } catch (Exception ex) {
                    log.debug("incomingStateCode not JSON or parse failed: {}", ex.getMessage());
                }
            }

            // If still not found, try resolving by name using the incomingStateCode string as a name
            if (stateMaster == null) {
                stateMaster = stateMasterRepository.getStateByName(codeCandidate);
                log.info("Fallback lookup by name for '{}' ? {}", codeCandidate, (stateMaster != null));
            }
        } else {
            // If the request contained a stateName field, try resolving by that too
            String incomingStateName = addOrUpdateFacilityRequest.getStateName();
            if (incomingStateName != null && !incomingStateName.trim().isEmpty()) {
                stateMaster = stateMasterRepository.getStateByName(incomingStateName.trim());
                log.info("Resolved stateMaster by stateName='{}' ? {}", incomingStateName, (stateMaster != null));
            } else {
                log.info("No stateCode or stateName provided in request; state will be set to null");
            }
        }

        facilityMaster.setFacilityName(addOrUpdateFacilityRequest.getFacilityName());
        facilityMaster.setFacilityStreet(addOrUpdateFacilityRequest.getFacilityStreet());
        facilityMaster.setStateCode(stateMaster);
        facilityMaster.setNoOfRooms(addOrUpdateFacilityRequest.getNoOfRooms());
        facilityMaster.setIsActive(true);
        facilityMasterRepository.save(facilityMaster);

        // delete existing mappings and re-insert using a native insert helper so
        // state_code (when present) is written explicitly.
    facilitySpecialityMappingRepository.deleteFacilityAllSpeciality(facilityMaster.getFacilityMasterId());

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
        // If the incoming request contained explicit roomDetails, echo those back
        // in the response so the FE can render per-room specialties without
        // requiring DB schema changes. The AddOrUpdateFacilityRequest may include
        // roomDetails as an array of objects with a specialityList; use that
        // when present.
        if (addOrUpdateFacilityRequest != null && addOrUpdateFacilityRequest.getRoomDetails() != null
                && !addOrUpdateFacilityRequest.getRoomDetails().isEmpty()) {
            try {
                List<RoomDetail> rd = new java.util.ArrayList<>();
                int idx = 0;
                for (Object raw : addOrUpdateFacilityRequest.getRoomDetails()) {
                    idx++;
                    if (raw == null) continue;
                    // raw is expected to be a Map-like structure (Jackson will have deserialized JSON)
                    if (raw instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) raw;
                        RoomDetail r = new RoomDetail();
                        Object rn = map.get("roomNumber");
                        if (rn instanceof Number) r.setRoomNumber(((Number) rn).intValue());
                        else r.setRoomNumber(idx);
                        Object rl = map.get("roomLabel");
                        r.setRoomLabel(rl != null ? String.valueOf(rl) : ("Room " + r.getRoomNumber()));
                        java.util.List<String> specIds = new java.util.ArrayList<>();
                        Object listObj = map.get("specialityList");
                        if (listObj instanceof java.util.List) {
                            for (Object o : (java.util.List<?>) listObj) {
                                if (o != null) specIds.add(String.valueOf(o));
                            }
                        }
                        r.setSpecialityList(specIds);
                        rd.add(r);
                    }
                }
                facilityDetails.setRoomDetails(rd);
            } catch (Exception e) {
                // Non-fatal: if parsing fails, omit roomDetails and continue
                // log at debug level
                log.debug("Failed to populate roomDetails from request: {}", e.getMessage());
            }
        }
        if (facilityMaster.getStateCode() != null) {
            facilityDetails.setStateCode(facilityMaster.getStateCode().getStateCode());
            facilityDetails.setStateName(facilityMaster.getStateCode().getStateName());
        } else {
            facilityDetails.setStateCode(null);
            facilityDetails.setStateName(null);
        }

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
