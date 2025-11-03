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

    private String last4;
    private String brand;
    private int expMonth;
    private int expYear;
}
