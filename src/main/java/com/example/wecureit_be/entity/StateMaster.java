package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "state_master", schema = "public")
public class StateMaster {

    @Id
    @Column(name = "state_code")
    public String stateCode;

    @Column(name = "state_name")
    public String stateName;
}
