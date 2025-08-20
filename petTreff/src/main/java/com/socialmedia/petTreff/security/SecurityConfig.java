package com.socialmedia.petTreff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
