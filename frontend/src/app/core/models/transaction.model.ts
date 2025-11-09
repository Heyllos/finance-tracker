export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export enum RecurrenceFrequency {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  YEARLY = 'YEARLY'
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  icon?: string;
  color?: string;
  type: TransactionType;
  isDefault?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Transaction {
  id: number;
  amount: number;
  description: string;
  notes?: string;
  transactionDate: string;
  type: TransactionType;
  category: Category;
  paymentMethod?: string;
  merchant?: string;
  receiptUrl?: string;
  isRecurring?: boolean;
  recurrenceFrequency?: RecurrenceFrequency;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRequest {
  amount: number;
  description: string;
  notes?: string;
  transactionDate: string;
  type: TransactionType;
  categoryId: number;
  paymentMethod?: string;
  merchant?: string;
  receiptUrl?: string;
  isRecurring?: boolean;
  recurrenceFrequency?: RecurrenceFrequency;
}
