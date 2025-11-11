export interface ExchangeRate {
  base: string;
  timestamp: string;
  rates: { [key: string]: number };
}

export interface CryptoPrice {
  symbol: string;
  name: string;
  priceUsd: number;
  priceEur: number;
  changePercent24h?: number;
  marketCapUsd?: number;
  volume24h?: number;
  timestamp: string;
}
