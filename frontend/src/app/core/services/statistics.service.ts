import { Injectable } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { TransactionService } from './transaction.service';
import { BudgetService } from './budget.service';
import { GoalService } from './goal.service';
import { Transaction, TransactionType } from '@core/models/transaction.model';
import { Budget } from '@core/models/budget.model';
import { Goal } from '@core/models/goal.model';

export interface DashboardStats {
  monthlyBalance: number;
  totalIncome: number;
  totalExpenses: number;
  savingsRate: number;
}

export interface CategoryExpense {
  categoryName: string;
  amount: number;
  color: string;
}

export interface MonthlyData {
  month: string;
  income: number;
  expenses: number;
}

export interface BudgetProgress {
  categoryName: string;
  allocated: number;
  spent: number;
  remaining: number;
  percentage: number;
  color: string;
}

export interface GoalProjection {
  goalName: string;
  current: number;
  projected: number;
  target: number;
  allocationPercentage: number;
}

export interface TopExpense {
  description: string;
  amount: number;
  categoryName: string;
  color: string;
  date: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  constructor(
    private transactionService: TransactionService,
    private budgetService: BudgetService,
    private goalService: GoalService
  ) {}

  getDashboardStats(): Observable<DashboardStats> {
    return this.transactionService.getAllTransactions().pipe(
      map(transactions => {
        const currentMonth = new Date().getMonth();
        const currentYear = new Date().getFullYear();
        
        const currentMonthTransactions = transactions.filter(t => {
          const date = new Date(t.transactionDate);
          return date.getMonth() === currentMonth && date.getFullYear() === currentYear;
        });

        const totalIncome = currentMonthTransactions
          .filter(t => t.type === TransactionType.INCOME)
          .reduce((sum, t) => sum + t.amount, 0);

        const totalExpenses = currentMonthTransactions
          .filter(t => t.type === TransactionType.EXPENSE)
          .reduce((sum, t) => sum + t.amount, 0);

        const monthlyBalance = totalIncome - totalExpenses;
        const savingsRate = totalIncome > 0 ? (monthlyBalance / totalIncome) * 100 : 0;

        return {
          monthlyBalance,
          totalIncome,
          totalExpenses,
          savingsRate
        };
      })
    );
  }

  getCategoryExpenses(): Observable<CategoryExpense[]> {
    return this.transactionService.getAllTransactions().pipe(
      map(transactions => {
        const currentMonth = new Date().getMonth();
        const currentYear = new Date().getFullYear();
        
        const currentMonthExpenses = transactions.filter(t => {
          const date = new Date(t.transactionDate);
          return date.getMonth() === currentMonth && 
                 date.getFullYear() === currentYear &&
                 t.type === TransactionType.EXPENSE;
        });

        const categoryMap = new Map<string, { amount: number, color: string }>();
        
        currentMonthExpenses.forEach(t => {
          const existing = categoryMap.get(t.category.name) || { amount: 0, color: t.category.color || '#999' };
          categoryMap.set(t.category.name, {
            amount: existing.amount + t.amount,
            color: t.category.color || '#999'
          });
        });

        return Array.from(categoryMap.entries()).map(([name, data]) => ({
          categoryName: name,
          amount: data.amount,
          color: data.color
        })).sort((a, b) => b.amount - a.amount);
      })
    );
  }

  getMonthlyTrends(months: number = 6): Observable<MonthlyData[]> {
    return this.transactionService.getAllTransactions().pipe(
      map(transactions => {
        const result: MonthlyData[] = [];
        const now = new Date();

        for (let i = months - 1; i >= 0; i--) {
          const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
          const month = date.toLocaleDateString('fr-FR', { month: 'short', year: '2-digit' });
          
          const monthTransactions = transactions.filter(t => {
            const tDate = new Date(t.transactionDate);
            return tDate.getMonth() === date.getMonth() && 
                   tDate.getFullYear() === date.getFullYear();
          });

          const income = monthTransactions
            .filter(t => t.type === TransactionType.INCOME)
            .reduce((sum, t) => sum + t.amount, 0);

          const expenses = monthTransactions
            .filter(t => t.type === TransactionType.EXPENSE)
            .reduce((sum, t) => sum + t.amount, 0);

          result.push({ month, income, expenses });
        }

        return result;
      })
    );
  }

  getBudgetProgress(): Observable<BudgetProgress[]> {
    return this.budgetService.getActiveBudgets().pipe(
      map(budgets => {
        return budgets.map(budget => ({
          categoryName: budget.category.name,
          allocated: budget.amount,
          spent: budget.spentAmount,
          remaining: budget.remainingAmount,
          percentage: budget.percentageUsed,
          color: budget.isOverBudget ? '#f44336' : 
                 budget.shouldAlert ? '#ff9800' : '#4caf50'
        })).sort((a, b) => b.percentage - a.percentage);
      })
    );
  }

  getGoalProjections(monthlySavings: number): Observable<GoalProjection[]> {
    return this.goalService.getActiveGoals().pipe(
      map(goals => {
        return goals
          .filter(g => g.allocationPercentage && g.allocationPercentage > 0)
          .map(goal => {
            const monthlyAllocation = (monthlySavings * (goal.allocationPercentage || 0)) / 100;
            const projected = goal.currentAmount + monthlyAllocation;
            
            return {
              goalName: goal.name,
              current: goal.currentAmount,
              projected: projected,
              target: goal.targetAmount,
              allocationPercentage: goal.allocationPercentage || 0
            };
          })
          .sort((a, b) => b.allocationPercentage - a.allocationPercentage);
      })
    );
  }

  getTopExpenses(limit: number = 5): Observable<TopExpense[]> {
    return this.transactionService.getAllTransactions().pipe(
      map(transactions => {
        const currentMonth = new Date().getMonth();
        const currentYear = new Date().getFullYear();
        
        const currentMonthExpenses = transactions.filter(t => {
          const date = new Date(t.transactionDate);
          return date.getMonth() === currentMonth && 
                 date.getFullYear() === currentYear &&
                 t.type === TransactionType.EXPENSE;
        });

        return currentMonthExpenses
          .map(t => ({
            description: t.description,
            amount: t.amount,
            categoryName: t.category.name,
            color: t.category.color || '#999',
            date: t.transactionDate
          }))
          .sort((a, b) => b.amount - a.amount)
          .slice(0, limit);
      })
    );
  }
}
