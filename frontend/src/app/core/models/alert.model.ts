export enum AlertType {
  BUDGET_EXCEEDED = 'BUDGET_EXCEEDED',
  BUDGET_WARNING = 'BUDGET_WARNING',
  GOAL_DEADLINE_APPROACHING = 'GOAL_DEADLINE_APPROACHING',
  UNUSUAL_SPENDING = 'UNUSUAL_SPENDING'
}

export enum AlertSeverity {
  INFO = 'INFO',
  WARNING = 'WARNING',
  CRITICAL = 'CRITICAL'
}

export interface Alert {
  id: number;
  type: AlertType;
  severity: AlertSeverity;
  title: string;
  message: string;
  budgetId?: number;
  budgetCategoryName?: string;
  goalId?: number;
  goalName?: string;
  isRead: boolean;
  createdAt: string;
}
