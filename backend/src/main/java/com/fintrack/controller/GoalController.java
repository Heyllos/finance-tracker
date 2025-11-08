package com.fintrack.controller;

import com.fintrack.dto.GoalRequest;
import com.fintrack.dto.GoalResponse;
import com.fintrack.entity.GoalStatus;
import com.fintrack.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des objectifs d'épargne.
 */
@RestController
@RequestMapping("/api/goals")
@PreAuthorize("hasRole('USER')")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals(Authentication authentication) {
        List<GoalResponse> goals = goalService.getAllGoals(authentication);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/active")
    public ResponseEntity<List<GoalResponse>> getActiveGoals(Authentication authentication) {
        List<GoalResponse> goals = goalService.getActiveGoals(authentication);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<GoalResponse>> getGoalsByStatus(
            @PathVariable GoalStatus status,
            Authentication authentication) {
        List<GoalResponse> goals = goalService.getGoalsByStatus(status, authentication);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(
            @PathVariable Long id,
            Authentication authentication) {
        GoalResponse goal = goalService.getGoalById(id, authentication);
        return ResponseEntity.ok(goal);
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody GoalRequest request,
            Authentication authentication) {
        GoalResponse goal = goalService.createGoal(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request,
            Authentication authentication) {
        GoalResponse goal = goalService.updateGoal(id, request, authentication);
        return ResponseEntity.ok(goal);
    }

    @PostMapping("/{id}/contribute")
    public ResponseEntity<GoalResponse> addContribution(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> payload,
            Authentication authentication) {
        BigDecimal amount = payload.get("amount");
        GoalResponse goal = goalService.addContribution(id, amount, authentication);
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            Authentication authentication) {
        goalService.deleteGoal(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
