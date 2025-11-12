export interface StockDataPoint {
  timestamp: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

export interface StockMetadata {
  information: string;
  symbol: string;
  lastRefreshed: string;
  interval: string;
  outputSize: string;
  timeZone: string;
}

export interface StockDataResponse {
  symbol: string;
  interval: string;
  lastRefreshed: string;
  timeSeries: StockDataPoint[];
  metadata?: StockMetadata;
  isDemo?: boolean;
}

export type StockInterval = '1min' | '5min' | '15min' | '30min' | '60min' | 'daily';

export interface StockSearchParams {
  symbol: string;
  interval: StockInterval;
}

// Métriques fondamentales
export interface CompanyOverview {
  symbol: string;
  name: string;
  description: string;
  sector: string;
  industry: string;
  country: string;
  exchange: string;
  currency: string;
  
  // Métriques financières
  marketCapitalization: number;
  peRatio: number;
  pegRatio: number;
  bookValue: number;
  dividendPerShare: number;
  dividendYield: number;
  eps: number;
  revenuePerShareTTM: number;
  profitMargin: number;
  operatingMarginTTM: number;
  returnOnAssetsTTM: number;
  returnOnEquityTTM: number;
  revenueTTM: number;
  grossProfitTTM: number;
  ebitda: number;
  
  // Métriques de trading
  fiftyTwoWeekHigh: number;
  fiftyTwoWeekLow: number;
  fiftyDayMovingAverage: number;
  twoHundredDayMovingAverage: number;
  sharesOutstanding: number;
  exDividendDate: string;
}

// Indicateurs techniques
export interface TechnicalIndicators {
  symbol: string;
  indicator: string;
  interval: string;
  lastRefreshed: string;
  
  // RSI
  rsi?: number;
  
  // MACD
  macd?: number;
  macdSignal?: number;
  macdHist?: number;
  
  // SMA/EMA
  sma?: { [key: string]: number };
  ema?: { [key: string]: number };
  
  // Stochastic
  stochK?: number;
  stochD?: number;
  
  // ADX
  adx?: number;
  
  // Bollinger Bands
  bollingerUpper?: number;
  bollingerMiddle?: number;
  bollingerLower?: number;
}

// Type de graphique
export type ChartType = 'line' | 'candlestick' | 'area';

// Configuration des indicateurs
export interface IndicatorConfig {
  name: string;
  enabled: boolean;
  color: string;
  params?: any;
}
