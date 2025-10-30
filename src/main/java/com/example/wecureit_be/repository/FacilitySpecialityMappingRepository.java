package com.example.wecureit_be.repository;


import com.example.wecureit_be.entity.FacilitySpecialityMapping;
import com.example.wecureit_be.entity.FacilitySpecialityMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FacilitySpecialityMappingRepository extends JpaRepository<FacilitySpecialityMapping, FacilitySpecialityMappingId> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM facility_speciality_mapping WHERE facility_master_id = :facilityMasterId ", nativeQuery = true)
    int deleteFacilityAllSpeciality(@Param("facilityMasterId") String facilityMasterId);

    @Query(value = "select * from facility_speciality_mapping " +
            "where facility_master_id = :facilityMasterId ", nativeQuery = true)
    List<FacilitySpecialityMapping> getSpecialityByFacilityId(@Param("facilityMasterId") String facilityMasterId);

}
