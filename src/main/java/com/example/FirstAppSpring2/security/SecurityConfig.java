package com.example.FirstAppSpring2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, AuthenticationProvider authProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/vehicles/available").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET,"/api/vehicles/rented").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET,"/api/rentals/rented").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST,"/api/rentals/rent").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST,"/api/rentals/return").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET,"/api/users/profile").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET,"/api/users/test-role").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("api/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                        //.requestMatchers(HttpMethod.POST,"/api/vehicles").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.GET,"/api/vehicles/available").hasRole("USER")
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}