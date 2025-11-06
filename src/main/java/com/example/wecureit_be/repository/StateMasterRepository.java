package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.StateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StateMasterRepository extends JpaRepository<StateMaster, String> {

    @Query(value = "SELECT * FROM state_master where state_code = :stateCode ", nativeQuery = true)
    StateMaster getStateById(@Param("stateCode") String stateCode);

}
