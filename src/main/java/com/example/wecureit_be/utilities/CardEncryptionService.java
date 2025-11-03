package com.example.wecureit_be.utilities;

import org.springframework.stereotype.Service;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

@Service
public class CardEncryptionService {

    private final LocalKmsService kmsService;
    private final SecureRandom random = new SecureRandom();

    public CardEncryptionService(LocalKmsService kmsService) {
        this.kmsService = kmsService;
    }

    public EncryptedCard encryptCard(String plainPan) throws Exception {
        // Generate DEK
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey dek = keyGen.generateKey();

        // Encrypt PAN with DEK
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, dek, new GCMParameterSpec(128, iv));
        byte[] cipherText = cipher.doFinal(plainPan.getBytes(StandardCharsets.UTF_8));

        // Wrap DEK using master key
        byte[] wrappedDek = kmsService.wrapKey(dek);
        Arrays.fill(plainPan.toCharArray(), '\0'); // clear sensitive data

        return new EncryptedCard(
            Base64.getEncoder().encodeToString(cipherText),
            Base64.getEncoder().encodeToString(wrappedDek),
            Base64.getEncoder().encodeToString(iv)
        );
    }

    public String decryptCard(String encPan, String wrappedDek, String iv) throws Exception {
        SecretKey dek = kmsService.unwrapKey(Base64.getDecoder().decode(wrappedDek));
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, dek, new GCMParameterSpec(128, Base64.getDecoder().decode(iv)));
        byte[] plainBytes = cipher.doFinal(Base64.getDecoder().decode(encPan));
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    public record EncryptedCard(String encryptedPan, String wrappedDek, String iv) {}
}
