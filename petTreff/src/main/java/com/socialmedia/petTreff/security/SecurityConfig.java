package com.socialmedia.petTreff.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // match your stored password hashes
    }

    // das ist Konfiguration für Postman, stateless, Basic Auth und keine
    // login-Formular

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF
                .cors(cors -> {}) // <— aktiviert CORS (holt Config unten)                     Das NEUE ÄNDERUNG
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/public/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> {
                }); // Basic Auth

        return http.build();
    }
                                                                ///          Das NEUE ÄNDERUNG
    // Globale CORS-Regel (ergänzt/übersteuert @CrossOrigin)
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${frontend.url}") String frontendUrl) {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of(frontendUrl)); // z.B. http://localhost:3000
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("Content-Type","Authorization"));
        c.setExposedHeaders(List.of("Location")); // optional
        c.setAllowCredentials(true); // nur nötig bei Cookies/Session
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }


    /*
     * // für Browser-Login (stateful session, CSRF bleibt aktiv)
     * 
     * @Bean
     * public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     * http
     * .authorizeHttpRequests(auth -> auth
     * .requestMatchers("/auth/register", "/error").permitAll()
     * .requestMatchers(HttpMethod.GET, "/public/**").permitAll()
     * .anyRequest().authenticated()
     * )
     * .formLogin(form -> form.permitAll()) // default login page
     * .logout(logout -> logout.permitAll()); // optional logout
     * 
     * // Notice: no .csrf(...) line at all, because it’s enabled by default
     * 
     * return http.build();
     * }
     */

}
