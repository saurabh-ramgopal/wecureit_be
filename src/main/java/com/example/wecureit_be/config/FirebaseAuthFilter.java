package com.example.wecureit_be.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        log.info("FirebaseAuthFilter processing request: {}", path);
        
        // Get Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            try {
                // Verify the Firebase token
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                
                // Extract user information from token
                String uid = decodedToken.getUid();
                String email = decodedToken.getEmail();
                
                // Get custom claims (userType: patient/doctor/admin)
                String userType = (String) decodedToken.getClaims().get("userType");
                
                log.info("Authenticated user: uid={}, email={}, userType={}", uid, email, userType);
                
                // Create authorities based on user type
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + (userType != null ? userType.toUpperCase() : "USER"))
                );
                
                // Set authentication in Spring Security context
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Store additional user info as attributes for later use
                request.setAttribute("firebaseUid", uid);
                request.setAttribute("userEmail", email);
                request.setAttribute("userType", userType);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.info("Successfully authenticated and set firebaseUid attribute: {}", uid);
                
            } catch (FirebaseAuthException e) {
                log.error("Firebase token verification failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                response.setContentType("application/json");
                return;
            } catch (Exception e) {
                log.error("Error processing Firebase token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Authentication error\"}");
                response.setContentType("application/json");
                return;
            }
        } else {
            log.warn("No Authorization header found for request: {}", path);
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filter for public endpoints that don't need Firebase auth at all
        String path = request.getRequestURI();
        return path.startsWith("/api/public/") || 
               path.equals("/api/common/login") || 
               path.startsWith("/actuator/");
    }
}
