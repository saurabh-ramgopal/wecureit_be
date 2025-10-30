package com.example.wecureit_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "admin_master", schema = "public")
public class AdminMaster {

    @Id
    @Column(name = "admin_master_id")
    public Integer adminMasterId;

    @Column(name = "admin_name")
    public String adminName;

}
