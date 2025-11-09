export enum GoalStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export enum GoalPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export interface Goal {
  id: number;
  name: string;
  description?: string;
  targetAmount: number;
  currentAmount: number;
  remainingAmount: number;
  percentageAchieved: number;
  targetDate?: string;
  status: GoalStatus;
  priority: GoalPriority;
  icon?: string;
  color?: string;
  isArchived: boolean;
  isCompleted: boolean;
  isOverdue: boolean;
  allocationPercentage?: number;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
}

export interface GoalRequest {
  name: string;
  description?: string;
  targetAmount: number;
  targetDate?: string;
  priority?: GoalPriority;
  icon?: string;
  color?: string;
  allocationPercentage?: number;
}
