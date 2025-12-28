package com.jeeva.calorietrackerbackend.config;


import com.jeeva.calorietrackerbackend.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;


import java.io.IOException;
import java.util.Collections;



public class RateLimitFilter implements Filter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RateLimitFilter.class);
    private static final int MAX_REQUESTS = 100;
    private static final int WINDOW_SECONDS = 300;

    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;


    public RateLimitFilter(StringRedisTemplate redisTemplate, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    private static final String LUA_SCRIPT =
            "local current = redis.call('INCR', KEYS[1]) " +
            "if current == 1 then " +
            "  redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
            "end " +
            "return current";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        log.info("RateLimit hit: method={}, path={}",
                req.getMethod(), req.getRequestURI());
        String path = req.getRequestURI();
        if (path.contains("/api/auth")
                || path.contains("/api/payment/create-order")
                || path.contains("/api/payment/verify")
                || path.contains("/api/payment/webhook")) {

            log.info("RateLimit Skipping for {}", path);
            chain.doFilter(req, res);
            return;
        }


        String token = extractToken(req);


        boolean isSubscribed = false;

        if (token != null) {
            try {
                isSubscribed = jwtUtil.isSubscribed(token);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("""
        {
          "error": "Session expired. Please login again."
        }
        """);
                return;
            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }
        }



        if (isSubscribed) {
            chain.doFilter(request, response);
            return;
        }


        String clientKey = buildRateLimitKey(req, token);

        Long count = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
                Collections.singletonList(clientKey),
                String.valueOf(WINDOW_SECONDS)
        );

        if (count != null && count > MAX_REQUESTS) {
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Too many requests\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String buildRateLimitKey(HttpServletRequest request, String token) {
        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            return "rate_limit:user:" + username;
        }
        return "rate_limit:ip:" + request.getRemoteAddr();
    }
}
