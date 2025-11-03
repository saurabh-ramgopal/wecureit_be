package com.example.wecureit_be.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * LocalKmsService securely loads the AES master key from the local keystore.
 * The keystore file is stored within the project (e.g., /keys/wecureit-keystore.jceks)
 * and used to wrap/unwrap per-card encryption keys.
 */
@Service
public class LocalKmsService {

    private final SecretKey masterKey;

    public LocalKmsService(
            @Value("${wecureit.keystore.path}") String keystorePath,
            @Value("${wecureit.keystore.password}") String keystorePassword,
            @Value("${wecureit.key.alias:wecureitMasterKey}") String keyAlias
    ) throws Exception {

        // Resolve path — supports relative paths (e.g., keys/wecureit-keystore.jceks)
        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists()) {
            throw new IllegalStateException("Keystore file not found at: " + keystoreFile.getAbsolutePath());
        }

        // Load keystore
        KeyStore ks = KeyStore.getInstance("JCEKS");
        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            ks.load(fis, keystorePassword.toCharArray());
        }

        // Load AES master key
        masterKey = (SecretKey) ks.getKey(keyAlias, keystorePassword.toCharArray());
        if (masterKey == null) {
            throw new IllegalStateException("No key found in keystore for alias: " + keyAlias);
        }

        System.out.println("LocalKmsService initialized using keystore: " + keystoreFile.getAbsolutePath());
    }

    /** Wraps (encrypts) a Data Encryption Key (DEK) using the master key. */
    public byte[] wrapKey(SecretKey dek) throws Exception {
        Cipher cipher = Cipher.getInstance("AESWrap");
        cipher.init(Cipher.WRAP_MODE, masterKey);
        return cipher.wrap(dek);
    }

    /** Unwraps (decrypts) a wrapped DEK using the master key. */
    public SecretKey unwrapKey(byte[] wrappedDek) throws Exception {
        Cipher cipher = Cipher.getInstance("AESWrap");
        cipher.init(Cipher.UNWRAP_MODE, masterKey);
        return (SecretKey) cipher.unwrap(wrappedDek, "AES", Cipher.SECRET_KEY);
    }
}
