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

        if (authHeader == null) {
            sendUnauthorized(response, "Authorization header is missing");
            return;
        }


        if (!authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Authorization header must start with Bearer");
            return;
        }

        final String jwt = authHeader.substring(7);
        log.debug("AuthHeader :{}", authHeader);
        log.debug("jwt :{}", jwt);
        log.debug("JWT length: {}, first 10 chars: {}", jwt.length(), jwt.substring(0, 10));
        String userEmail = null;
        try {
            userEmail = jwtUtil.extractUsername(jwt);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.debug("Session Expired {} ", jwt);
            sendUnauthorized(response, "Session expired. Please login again.");
            return;
        } catch (Exception e) {
            log.debug("Invalid token {} ", jwt);
            sendUnauthorized(response, "Invalid token");
            return;
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

    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
        {
          "error": "UNAUTHORIZED",
          "message": "%s"
        }
        """.formatted(message));
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/auth/login")||path.startsWith("/api/auth/register")
                || path.startsWith("/api/payment/webhook");
    }
}
