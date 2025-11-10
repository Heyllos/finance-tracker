package com.fintrack.repository;

import com.fintrack.entity.Alert;
import com.fintrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour gérer les alertes.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    /**
     * Trouve toutes les alertes d'un utilisateur, triées par date de création décroissante.
     */
    List<Alert> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Trouve les alertes non lues d'un utilisateur.
     */
    List<Alert> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    /**
     * Compte le nombre d'alertes non lues d'un utilisateur.
     */
    Long countByUserAndIsReadFalse(User user);
    
    /**
     * Supprime toutes les alertes d'un utilisateur.
     */
    void deleteByUser(User user);
}
