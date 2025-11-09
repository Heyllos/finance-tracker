import { Category } from './transaction.model';

export interface Budget {
  id: number;
  name: string;
  amount: number;
  spentAmount: number;
  remainingAmount: number;
  percentageUsed: number;
  startDate: string;
  endDate: string;
  category: Category;
  isRecurring?: boolean;
  recurrenceFrequency?: string;
  alertEnabled?: boolean;
  alertThreshold?: number;
  isActive: boolean;
  isOverBudget: boolean;
  shouldAlert: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface BudgetRequest {
  name: string;
  amount: number;
  startDate: string;
  endDate: string;
  categoryId: number;
  isRecurring?: boolean;
  recurrenceFrequency?: string;
  alertEnabled?: boolean;
  alertThreshold?: number;
}
