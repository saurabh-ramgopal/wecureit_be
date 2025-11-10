package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.Card;
import com.example.wecureit_be.impl.CardService;
import com.example.wecureit_be.request.CardRequest;
import com.example.wecureit_be.response.CardResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add")
    public ResponseEntity<CardResponse> addCard(@RequestBody CardRequest req) throws Exception {
        Card card = cardService.addCard(req);
        URI location = URI.create("/cards/" + card.getId());
        // return only id and last4 in response (see CardResponse)
        return ResponseEntity.created(location).body(new CardResponse(card.getId(), card.getLast4()));
    }

    @GetMapping("/{id}")
    public String viewCard(@PathVariable Long id) throws Exception {
        return cardService.viewCard(id);
    }

    @GetMapping("/{id}/masked")
    public String viewMaskedCard(@PathVariable Long id) throws Exception {
        String pan = cardService.viewCard(id);
        return "**** **** **** " + pan.substring(pan.length() - 4);
    }

    @GetMapping("/getcards")
    public List<String> getCards(@RequestParam(name = "patientId") Integer patientMasterId) {
        return cardService.getCardsByPatientId(patientMasterId);
    }


}
