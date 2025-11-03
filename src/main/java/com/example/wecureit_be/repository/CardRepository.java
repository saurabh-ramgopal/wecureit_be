package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {}
