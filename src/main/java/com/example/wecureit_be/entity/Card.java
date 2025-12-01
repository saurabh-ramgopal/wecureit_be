package com.example.wecureit_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String encryptedPan;

    @Column(columnDefinition = "TEXT")
    private String wrappedDek;

    @Column(columnDefinition = "TEXT")
    private String iv;

    @Column(columnDefinition = "TEXT")
    private String encryptedCvc;

    @Column(columnDefinition = "TEXT")
    private String cvcIv;

    private String last4;
    @Column(columnDefinition = "TEXT")
    private String encryptedExpiry;

    @Column(columnDefinition = "TEXT")
    private String expiryIv;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "patient_master_id", referencedColumnName = "patient_master_id")
    private PatientMaster patientMaster;
}
