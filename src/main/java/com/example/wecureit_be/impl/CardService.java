package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.Card;
import com.example.wecureit_be.repository.CardRepository;
import com.example.wecureit_be.request.CardRequest;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.utilities.CardEncryptionService;


import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
        // fetch PatientMaster and set the relation
        PatientMaster pm = patientController.getById(req.getPatientMasterId());

        // Check for duplicate PAN for this patient by decrypting existing cards
        var existingCards = repo.findAllByPatientMasterPatientMasterIdOrderByIdAsc(req.getPatientMasterId());
        for (Card existing : existingCards) {
            String existingPan = encryptService.decryptCard(existing.getEncryptedPan(), existing.getWrappedDek(), existing.getIv());
            if (existingPan != null && existingPan.equals(req.getPan())) {
                throw new Exception("Card with same PAN already exists for this patient");
            }
        }

    // Encrypt PAN and CVC using same DEK but unique IV per field
    var multi = encryptService.encryptMultiple(req.getPan(), req.getCvc());
    String[] encryptedVals = multi.encryptedValues();
    String[] ivs = multi.ivs();

    Card card = new Card();
    // ensure new card is active by default
    card.setIsActive(true);
    card.setEncryptedPan(encryptedVals.length > 0 ? encryptedVals[0] : null);
    card.setEncryptedCvc(encryptedVals.length > 1 ? encryptedVals[1] : null);
    card.setWrappedDek(multi.wrappedDek());
    // store IVs per-field
    card.setIv(ivs.length > 0 ? ivs[0] : null);
    card.setCvcIv(ivs.length > 1 ? ivs[1] : null);
        card.setLast4(req.getPan().substring(req.getPan().length() - 4));
        // card.setBrand(req.getBrand()); --- IGNORE ---
        card.setExpMonth(req.getExpMonth());
        card.setExpYear(req.getExpYear());
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

    public void deleteCard(Integer patientMasterId) throws Exception{
        // Handle case where no cards exist for patient
        List<Card> cards = repo.findAllByPatientMasterPatientMasterIdOrderByIdAsc(patientMasterId);
        if (cards == null || cards.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cards found for patient id: " + patientMasterId);
        }
        repo.softDeleteById(patientMasterId);
    }
}
