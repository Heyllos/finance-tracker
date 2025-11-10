package com.fintrack.entity;

/**
 * Types d'alertes disponibles dans le système.
 */
public enum AlertType {
    BUDGET_EXCEEDED,              // Budget dépassé
    BUDGET_WARNING,               // Alerte budget (>80%)
    GOAL_DEADLINE_APPROACHING,    // Échéance d'objectif proche
    UNUSUAL_SPENDING              // Dépense inhabituelle
}
