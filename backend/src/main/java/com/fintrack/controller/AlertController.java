package com.fintrack.controller;

import com.fintrack.dto.AlertResponse;
import com.fintrack.entity.User;
import com.fintrack.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour gérer les alertes.
 */
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertController {

    @Autowired
    private AlertService alertService;

    /**
     * Récupère toutes les alertes de l'utilisateur connecté.
     */
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(alertService.getAllAlerts(user));
    }

    /**
     * Récupère les alertes non lues de l'utilisateur connecté.
     */
    @GetMapping("/unread")
    public ResponseEntity<List<AlertResponse>> getUnreadAlerts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(alertService.getUnreadAlerts(user));
    }

    /**
     * Récupère le nombre d'alertes non lues.
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        Long count = alertService.getUnreadCount(user);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Génère toutes les alertes (budget + objectifs).
     */
    @PostMapping("/generate")
    public ResponseEntity<Void> generateAlerts(@AuthenticationPrincipal User user) {
        alertService.generateAllAlerts(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Marque une alerte comme lue.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        alertService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Marque toutes les alertes comme lues.
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        alertService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime une alerte.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id, @AuthenticationPrincipal User user) {
        alertService.deleteAlert(id, user);
        return ResponseEntity.ok().build();
    }
}
