package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {


    // Derived query method (preferred) - returns all cards for a given patient id
    @Query("SELECT c.last4 FROM Card c WHERE c.patientMaster.patientMasterId = :patientMasterId ORDER BY c.id ASC")
    List<String> findByPatientMasterPatientMasterIdOrderByIdAsc(Integer patientMasterId);

}
