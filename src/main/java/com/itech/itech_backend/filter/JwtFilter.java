package com.itech.itech_backend.filter;

import com.itech.itech_backend.modules.shared.service.UserDetailsServiceImpl;
import com.itech.itech_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Lazy
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                log.error("Invalid JWT Token: {}", e.getMessage());
                // Continue with the request - Spring Security will handle authentication failure
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(jwtToken)) {
                    String role = jwtUtil.extractRole(jwtToken);
                    if (role != null) {
                        // Handle role with or without ROLE_ prefix
                        String authorityRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        username, null, List.of(new SimpleGrantedAuthority(authorityRole)
        ));

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("User {} authenticated with role {}", username, authorityRole);
                    } else {
                        log.error("No role found in JWT token for user {}", username);
                    }
                } else {
                    log.error("JWT token validation failed for user {}", username);
                }
            } catch (Exception e) {
                log.error("Error processing JWT token for user {}: {}", username, e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
