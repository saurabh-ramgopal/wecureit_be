package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.Card;
import com.example.wecureit_be.repository.CardRepository;
import com.example.wecureit_be.request.CardRequest;
import com.example.wecureit_be.utilities.CardEncryptionService;


import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final CardRepository repo;
    private final CardEncryptionService encryptService;

    public CardService(CardRepository repo, CardEncryptionService encryptService) {
        this.repo = repo;
        this.encryptService = encryptService;
    }

    public Card addCard(CardRequest req) throws Exception {
        var encrypted = encryptService.encryptCard(req.getPan());
        Card card = new Card();
        card.setEncryptedPan(encrypted.encryptedPan());
        card.setWrappedDek(encrypted.wrappedDek());
        card.setIv(encrypted.iv());
        card.setLast4(req.getPan().substring(req.getPan().length() - 4));
        card.setBrand(req.getBrand());
        card.setExpMonth(req.getExpMonth());
        card.setExpYear(req.getExpYear());
        return repo.save(card);
    }

    public String viewCard(Long id) throws Exception {
        Card card = repo.findById(id).orElseThrow();
        return encryptService.decryptCard(card.getEncryptedPan(), card.getWrappedDek(), card.getIv());
    }
}
