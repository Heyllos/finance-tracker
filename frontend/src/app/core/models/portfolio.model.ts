export enum StockTransactionType {
  BUY = 'BUY',
  SELL = 'SELL'
}

export interface StockTransaction {
  id?: number;
  symbol: string;
  companyName: string;
  type: StockTransactionType;
  quantity: number;
  pricePerShare: number;
  totalAmount: number;
  fees?: number;
  transactionDate: string;
  notes?: string;
  createdAt?: string;
}

export interface StockTransactionRequest {
  symbol: string;
  companyName: string;
  type: StockTransactionType;
  quantity: number;
  pricePerShare: number;
  fees?: number;
  transactionDate: string;
  notes?: string;
}

export interface PortfolioPosition {
  id: number;
  symbol: string;
  companyName: string;
  totalQuantity: number;
  averageBuyPrice: number;
  totalInvested: number;
  currentPrice?: number;
  currentValue?: number;
  gainLoss?: number;
  gainLossPercent?: number;
  updatedAt: string;
}

export interface TopPerformer {
  symbol: string;
  companyName: string;
  gainLossPercent: number;
  gainLoss: number;
}

export interface PortfolioSummary {
  totalInvested: number;
  currentValue: number;
  totalGainLoss: number;
  totalGainLossPercent: number;
  numberOfPositions: number;
  positions: PortfolioPosition[];
  topGainers: TopPerformer[];
  topLosers: TopPerformer[];
}
