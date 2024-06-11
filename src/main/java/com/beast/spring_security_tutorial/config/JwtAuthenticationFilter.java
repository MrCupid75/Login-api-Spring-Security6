package com.beast.spring_security_tutorial.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.beast.spring_security_tutorial.user.User;
import com.beast.spring_security_tutorial.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain)
            throws ServletException, IOException {
      
               String authHeader = request.getHeader("Authorization"); 

               if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
               }

               String jwtToken = authHeader.split(" ")[1];

               String username = jwtService.extractUsername(jwtToken);

               User user = userRepository.findByUsername(username).get();

               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, 
                null, 
                user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
    }

}
