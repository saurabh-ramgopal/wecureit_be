package com.example.wecureit_be.config;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final FirebaseToken firebaseToken;
    private final String principal;

    public FirebaseAuthenticationToken(FirebaseToken firebaseToken,
                                       String principal,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.firebaseToken = firebaseToken;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public FirebaseToken getFirebaseToken() {
        return firebaseToken;
    }
}