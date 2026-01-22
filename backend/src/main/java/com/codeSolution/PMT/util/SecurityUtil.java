package com.codeSolution.PMT.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtil {
    
    /**
     * Récupère l'ID de l'utilisateur actuellement authentifié depuis le contexte Spring Security
     * @return L'UUID de l'utilisateur authentifié, ou null si aucun utilisateur n'est authentifié
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        
        return null;
    }
    
    /**
     * Vérifie si un utilisateur est actuellement authentifié
     * @return true si un utilisateur est authentifié, false sinon
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && authentication.getPrincipal() instanceof UUID;
    }
}
