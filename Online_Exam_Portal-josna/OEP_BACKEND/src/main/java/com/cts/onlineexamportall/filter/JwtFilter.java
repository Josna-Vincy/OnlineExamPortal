package com.cts.onlineexamportall.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cts.onlineexamportall.service.CustomUserDetailsService;
import com.cts.onlineexamportall.service.JwtUtilService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtilService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        UUID userId = null;

        if (authHeader == null) {
            logger.warn("Authorization header is missing");
        } else if (!authHeader.toLowerCase().startsWith("bearer ")) {
            logger.warn("Authorization header is invalid: {}", authHeader);
        } else {
            logger.info("Authorization header is valid: {}", authHeader);
            try {
                token = authHeader.substring(7);
                username = jwtService.extractUserName(token);
                userId = UUID.fromString(jwtService.extractUserId(token));

                logger.info("Extracting token from header: {}", authHeader);
                logger.info("Processing token for username: {}", username);
            } catch (Exception e) {
                logger.error("Error processing token: {}", e.getMessage());
            }
        }

        if (username != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentication successful for username: {}", username);
            } else {
                logger.warn("Token validation failed for username: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}