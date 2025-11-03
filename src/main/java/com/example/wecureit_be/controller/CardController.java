package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.Card;
import com.example.wecureit_be.impl.CardService;
import com.example.wecureit_be.request.CardRequest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add")
    public Card addCard(@RequestBody CardRequest req) throws Exception {
        return cardService.addCard(req);
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


}
