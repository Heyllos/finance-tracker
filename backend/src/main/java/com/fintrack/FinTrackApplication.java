package com.fintrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe principale de l'application FinTrack.
 * 
 * Point d'entrée de l'application Spring Boot.
 * Active les fonctionnalités de scanning des composants, configuration automatique,
 * et auditing JPA pour les timestamps automatiques (createdAt, updatedAt).
 * 
 * @author FinTrack Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FinTrackApplication {

    /**
     * Méthode principale pour démarrer l'application Spring Boot.
     * 
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(FinTrackApplication.class, args);
    }
}
