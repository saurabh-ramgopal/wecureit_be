package com.example.wecureit_be.config;

import com.example.wecureit_be.service.CustomUserDetailsService;
import com.example.wecureit_be.utilities.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            logger.debug("Authorization header found; attempting to validate token (first30)={}", token.length() > 30 ? token.substring(0,30) + "..." : token);
            try {
                if (jwtUtil.validateToken(token)) {
                    username = jwtUtil.getSubjectFromToken(token);
                    logger.debug("Token validated; subject={}", username);
                } else {
                    logger.debug("Token validation failed");
                }
            } catch (Exception ex) {
                logger.debug("Exception while validating token: {}", ex.getMessage());
            }
        } else {
            logger.debug("No Bearer token found in request header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("Authentication set in SecurityContext for user={}", username);
                } else {
                    logger.debug("Token invalid at post-loadUserByUsername stage");
                }
            } catch (Exception ex) {
                logger.debug("Error loading user or setting authentication: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
