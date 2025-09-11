
package com.socialmedia.petTreff.security;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
        return new BCryptPasswordEncoder();
    }



     // filterChain and corsConfigurationSource for DEVELOPING

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // allow the WS handshake and SockJS without CSRF
                .csrf(csrf -> csrf
                        .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/ws/**", "/ws-sockjs/**", "/auth/login", "/auth/logout"))
                .cors(cors -> {}) // picks up the bean below
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/auth/register", "/auth/login", "/auth/logout",
                                "/error",
                                "/ws/**", "/ws-sockjs/**"          // handshake must be public
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(fl -> fl
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((req, res, authn) -> res.setStatus(HttpServletResponse.SC_OK))
                        .failureHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credentials"))
                        .permitAll()
                )
                .logout(lo -> lo
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT))
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    // CORS: allow your frontend + WebSocket King
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${frontend.url:http://localhost:*}") String frontendUrl
    ) {
        var c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of(
                frontendUrl,                     // your app (e.g. http://localhost:3000)
                "http://localhost:*"            // local tools
        ));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("Location"));
        c.setAllowCredentials(true);

        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }




    /* filterChain and corsConfigurationSource for DEVELOPING

    // das ist Konfiguration für Postman, stateless, Basic Auth und keine
    // login-Formular

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF nur für login/logout ignorieren
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/auth/login", "/auth/logout", "/ws/**", "/ws-sockjs/**")
                )
                .cors(cors -> {}) // <— aktiviert CORS (holt Config unten)                     Das NEUE ÄNDERUNG
                // Session-basiert (kein stateless!)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/register", "/auth/login",  "/auth/logout",
                                        "/error",  "/ws/**", "/ws-sockjs/**").permitAll()
                                .anyRequest().authenticated()
                        )
                // Login-Endpoint
                .formLogin(fl -> fl
                        .loginProcessingUrl("/auth/login")
                        .successHandler((req, res, authn) -> res.setStatus(HttpServletResponse.SC_OK))
                        .failureHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credentials"))
                        .permitAll()
                )

                // Logout-Endpoint
                .logout(lo -> lo
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT))
                        .permitAll()
                );

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


     */


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


