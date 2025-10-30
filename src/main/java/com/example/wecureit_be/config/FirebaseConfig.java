package com.example.wecureit_be.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Check if FirebaseApp has already been initialized
            if (FirebaseApp.getApps().isEmpty()) {
                // Option 1: Using service account JSON file (recommended for production)
                // Place your firebase-service-account.json in src/main/resources
                try {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(
                                    new ClassPathResource("firebase-service-account.json").getInputStream()))
                            .build();
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase Admin SDK initialized successfully with service account");
                } catch (Exception e) {
                    // Option 2: Using Application Default Credentials (for local development)
                    // This will use GOOGLE_APPLICATION_CREDENTIALS environment variable
                    log.warn("Service account file not found, attempting to use default credentials");
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .build();
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase Admin SDK initialized successfully with default credentials");
                }
            } else {
                log.info("Firebase Admin SDK already initialized");
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase Admin SDK: {}", e.getMessage());
            // Note: In production, you might want to throw a fatal error here
            // For development, we'll allow the app to start without Firebase
            log.error("Application will start without Firebase authentication enabled");
        }
    }
}
