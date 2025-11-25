package com.jeeva.calorietrackerbackend.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, @Lazy UserDetailsService userDetailsService){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug("JwtAuthFilter: Processing request {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Authorization header or not Bearer token, skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        log.debug("AuthHeader :{}", authHeader);
        log.debug("jwt :{}", jwt);
        log.debug("JWT length: {}, first 10 chars: {}", jwt.length(), jwt.substring(0, 10));
        String userEmail = null;
        try {
            userEmail = jwtUtil.extractUsername(jwt);
            log.debug("Extracted userEmail from JWT: {}", userEmail);
        } catch (Exception e) {
            log.warn("Failed to extract username from JWT: {}", e.getMessage());
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if(jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("User {} authenticated successfully", userEmail);
                } else {
                    log.warn("JWT validation failed for user {}", userEmail);
                }
            } catch (Exception e) {
                log.error("Error during user authentication for {}: {}", userEmail, e.getMessage());
            }
        } else {
            log.debug("User already authenticated or userEmail is null");
        }

        filterChain.doFilter(request, response);
        log.debug("JwtAuthFilter: Request processing finished for {}", request.getRequestURI());
    }
}
