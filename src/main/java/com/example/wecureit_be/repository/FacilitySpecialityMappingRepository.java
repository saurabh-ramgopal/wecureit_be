package com.example.wecureit_be.repository;


import com.example.wecureit_be.entity.FacilitySpecialityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FacilitySpecialityMappingRepository extends JpaRepository<FacilitySpecialityMapping, String> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM facility_speciality_mapping WHERE facility_master_id = :facilityMasterId ", nativeQuery = true)
    int deleteFacilityAllSpeciality(@Param("facilityMasterId") String facilityMasterId);

    @Query(value = "select * from facility_speciality_mapping " +
            "where facility_master_id = :facilityMasterId ", nativeQuery = true)
    List<FacilitySpecialityMapping> getSpecialityByFacilityId(@Param("facilityMasterId") String facilityMasterId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO public.facility_speciality_mapping (facility_master_id, speciality_master_id, facility_speciality_mapping_id) " +
            "VALUES( :facilityMasterId , :specialityMasterId , :facilitySpecialityMappingId );", nativeQuery = true)
    void insertIntoFacilitySpecialityMapping(@Param("facilityMasterId") String facilityMasterId,
                                             @Param("specialityMasterId") String specialityMasterId,
                                             @Param("facilitySpecialityMappingId") String facilitySpecialityMappingId);

    @Query(value = "select * from facility_speciality_mapping " +
            "where speciality_master_id = :specialityMasterId ", nativeQuery = true)
    List<FacilitySpecialityMapping> getFacilityBySpecialityId(@Param("specialityMasterId") String specialityMasterId);


}
