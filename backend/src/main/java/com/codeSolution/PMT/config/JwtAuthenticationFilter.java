package com.codeSolution.PMT.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String userId = request.getHeader("X-User-Id");
        String requestPath = request.getRequestURI();

        if (userId != null && !userId.isEmpty()) {
            try {
                UUID userUuid = UUID.fromString(userId.trim());
                
                if (sessionService.isValidUser(userUuid)) {
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userUuid,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Log seulement en debug pour éviter la verbosité
                    if (logger.isDebugEnabled()) {
                        logger.debug("Auth OK: user=" + userUuid + ", path=" + requestPath);
                    }
                } else {
                    logger.warn("User not found: userId=" + userId + ", path=" + requestPath);
                    // On laisse passer pour que Spring Security gère l'erreur 403
                }
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID: userId='" + userId + "', path=" + requestPath + ", error=" + e.getMessage());
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No X-User-Id header: path=" + requestPath);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
