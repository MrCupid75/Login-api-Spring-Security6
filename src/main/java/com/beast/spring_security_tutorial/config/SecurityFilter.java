package com.beast.spring_security_tutorial.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import com.beast.spring_security_tutorial.user.Permissions;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilter {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authConfig -> {
                authConfig.requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll();
                authConfig.requestMatchers(HttpMethod.POST, "/auth/register").permitAll();
                authConfig.requestMatchers("/error").permitAll();

                authConfig.requestMatchers(HttpMethod.GET, "/products").hasAuthority(Permissions.READ_ALL_PRODUCTS.name());
                authConfig.requestMatchers(HttpMethod.POST, "/products").hasAuthority(Permissions.SAVE_ONE_PRODUCT.name());

                authConfig.anyRequest().denyAll();

             } );
        return http.build();
    }
    
}
