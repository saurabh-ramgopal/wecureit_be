package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.DoctorFacilityAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorFacilityAvailabilityRepository extends JpaRepository<DoctorFacilityAvailability, Integer> {
}