package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.Card;
import com.example.wecureit_be.repository.CardRepository;
import com.example.wecureit_be.request.CardRequest;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.utilities.CardEncryptionService;


import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CardService {

    private final CardRepository repo;
    private final CardEncryptionService encryptService;
    private final PatientControllerImpl patientController;

    public CardService(CardRepository repo, CardEncryptionService encryptService, PatientControllerImpl patientController) {
        this.repo = repo;
        this.encryptService = encryptService;
        this.patientController = patientController;
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
        // fetch PatientMaster and set the relation
        PatientMaster pm = patientController.getById(req.getPatientMasterId());
        card.setPatientMaster(pm);
        return repo.save(card);
    }

    public String viewCard(Long id) throws Exception {
        Card card = repo.findById(id).orElseThrow();
        return encryptService.decryptCard(card.getEncryptedPan(), card.getWrappedDek(), card.getIv());
    }

    public List<String> getCardsByPatientId(Integer patientMasterId) {
        // use derived query to ensure all matching cards are returned and ordered
        return repo.findByPatientMasterPatientMasterIdOrderByIdAsc(patientMasterId);
    }
}
