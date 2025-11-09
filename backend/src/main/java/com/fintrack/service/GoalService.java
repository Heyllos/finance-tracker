package com.fintrack.service;

import com.fintrack.dto.GoalRequest;
import com.fintrack.dto.GoalResponse;
import com.fintrack.entity.Goal;
import com.fintrack.entity.GoalStatus;
import com.fintrack.entity.User;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.exception.UnauthorizedException;
import com.fintrack.repository.GoalRepository;
import com.fintrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    public List<GoalResponse> getAllGoals(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return goalRepository.findByUserId(user.getId())
                .stream()
                .map(GoalResponse::new)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getActiveGoals(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return goalRepository.findByUserIdAndIsArchived(user.getId(), false)
                .stream()
                .map(GoalResponse::new)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getGoalsByStatus(GoalStatus status, Authentication authentication) {
        User user = getCurrentUser(authentication);
        return goalRepository.findActiveGoalsByUserIdAndStatus(user.getId(), status)
                .stream()
                .map(GoalResponse::new)
                .collect(Collectors.toList());
    }

    public GoalResponse getGoalById(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Goal goal = goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Objectif non trouvé avec l'ID: " + id));
        return new GoalResponse(goal);
    }

    public GoalResponse createGoal(GoalRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Goal goal = new Goal();
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setPriority(request.getPriority());
        goal.setIcon(request.getIcon());
        goal.setColor(request.getColor());
        goal.setAllocationPercentage(request.getAllocationPercentage());
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);
        return new GoalResponse(savedGoal);
    }

    public GoalResponse updateGoal(Long id, GoalRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Goal goal = goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Objectif non trouvé avec l'ID: " + id));

        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setPriority(request.getPriority());
        goal.setIcon(request.getIcon());
        goal.setColor(request.getColor());
        goal.setAllocationPercentage(request.getAllocationPercentage());

        Goal updatedGoal = goalRepository.save(goal);
        return new GoalResponse(updatedGoal);
    }

    public GoalResponse addContribution(Long id, BigDecimal amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Goal goal = goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Objectif non trouvé avec l'ID: " + id));

        goal.addContribution(amount);
        Goal updatedGoal = goalRepository.save(goal);
        return new GoalResponse(updatedGoal);
    }

    public void deleteGoal(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Goal goal = goalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Objectif non trouvé avec l'ID: " + id));
        goalRepository.delete(goal);
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
    }
}
