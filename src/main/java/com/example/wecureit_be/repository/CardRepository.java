package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {


    // Derived query method (preferred) - returns all cards for a given patient id
    @Query("SELECT c.last4 FROM Card c WHERE c.patientMaster.patientMasterId = :patientMasterId AND c.isActive = true ORDER BY c.id ASC")
    List<String> findByPatientMasterPatientMasterIdOrderByIdAsc(Integer patientMasterId);

    // Fetch full Card entities for a given patient (used for duplicate checks)
    @Query("SELECT c FROM Card c WHERE c.patientMaster.patientMasterId = :patientMasterId AND c.isActive = true ORDER BY c.id ASC")
    List<Card> findAllByPatientMasterPatientMasterIdOrderByIdAsc(Integer patientMasterId);

    // Soft delete can be implemented here if needed
    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.isActive = false WHERE c.patientMaster.patientMasterId = :patientMasterId")
    void softDeleteById(@Param("patientMasterId") Integer patientMasterId);

}
