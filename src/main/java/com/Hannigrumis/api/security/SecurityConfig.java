package com.Hannigrumis.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizedRequests -> 
                authorizedRequests
                .requestMatchers( "/category/all", "/product/all", "/images/**", "/login", "/verify", "/reset-password", "/swagger-ui/**", "/bus/v3/**", "/api-docs/**", "/v3/**")
                .permitAll()
                .requestMatchers("/register")
                .hasRole("ADMIN")
                .anyRequest()
                .hasAnyRole("COLLABORATOR", "ADMIN"))
            .formLogin(form -> form.disable())
            .exceptionHandling(exceptionHandling -> 
                exceptionHandling.authenticationEntryPoint(unauthorizedHandler)
            );

        http.addFilterBefore(authenticationJwTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetails();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public AuthTokenFilter authenticationJwTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new CorsConfig();
    }
}
