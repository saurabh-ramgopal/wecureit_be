package com.example.wecureit_be.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/**").permitAll() // allow your API endpoints
//                        .anyRequest().permitAll()
//                );
//        return http.build();
//    }

    private final FirebaseTokenFilter firebaseTokenFilter;

    public SecurityConfig(FirebaseTokenFilter firebaseTokenFilter) {
        this.firebaseTokenFilter = firebaseTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/patient/registration").permitAll() // skipping auth endpoint
                        .requestMatchers("/admin/register").permitAll() // skipping auth endpoint
                        .requestMatchers("/admin/addDoctor").permitAll() // skipping auth endpoint
                        .requestMatchers("/**").permitAll() // skipping auth endpoint
                        .anyRequest().authenticated()
                )
                // Add our custom Firebase filter before the standard login filter
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
