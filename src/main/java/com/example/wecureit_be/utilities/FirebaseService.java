package com.example.wecureit_be.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FirebaseService {

    /**
     * Create a new Firebase user
     * @param email User email
     * @param password User password
     * @param userType Type of user (patient, doctor, admin)
     * @return Firebase UID
     */
    public String createFirebaseUser(String email, String password, String userType) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(false);
        
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();
        
        log.info("Created Firebase user: uid={}, email={}", uid, email);
        
        // Set custom claims for user type
        setUserTypeClaim(uid, userType);
        
        return uid;
    }
    
    /**
     * Set custom claim for user type (patient, doctor, admin)
     */
    public void setUserTypeClaim(String uid, String userType) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType);
        
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
        log.info("Set custom claims for user: uid={}, userType={}", uid, userType);
    }
    
    /**
     * Update user password
     */
    public void updateUserPassword(String uid, String newPassword) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPassword(newPassword);
        
        FirebaseAuth.getInstance().updateUser(request);
        log.info("Updated password for user: uid={}", uid);
    }
    
    /**
     * Delete Firebase user
     */
    public void deleteFirebaseUser(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(uid);
        log.info("Deleted Firebase user: uid={}", uid);
    }
    
    /**
     * Get user by email
     */
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByEmail(email);
    }
    
    /**
     * Verify if Firebase user exists
     */
    public boolean userExists(String email) {
        try {
            FirebaseAuth.getInstance().getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            return false;
        }
    }
}
