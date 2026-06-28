package com.esgis2026.assigame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Fournit l'encodeur de mot de passe (BCrypt) utilisé pour l'inscription et la connexion.
 * On n'active volontairement pas Spring Security complet (pas de filtre web, pas de session)
 * afin de garder l'API simple comme dans le reste du projet : seul le hachage du mot de passe
 * est ajouté pour ne plus stocker les mots de passe en clair en base.
 */
@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
