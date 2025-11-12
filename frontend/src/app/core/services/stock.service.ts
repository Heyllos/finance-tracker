import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockDataResponse, StockInterval, CompanyOverview, TechnicalIndicators } from '../models/stock.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  private apiUrl = `${environment.apiUrl}/stocks`;

  constructor(private http: HttpClient) {}

  /**
   * Récupère les données intraday pour un symbole boursier.
   * 
   * @param symbol Symbole boursier (ex: AAPL, TSLA, MSFT)
   * @param interval Intervalle de temps
   * @returns Observable des données boursières
   */
  getStockData(symbol: string, interval: StockInterval = '15min'): Observable<StockDataResponse> {
    const params = new HttpParams().set('interval', interval);
    return this.http.get<StockDataResponse>(`${this.apiUrl}/${symbol.toUpperCase()}`, { params });
  }

  /**
   * Récupère les données quotidiennes pour un symbole boursier.
   * 
   * @param symbol Symbole boursier
   * @returns Observable des données boursières quotidiennes
   */
  getDailyStockData(symbol: string): Observable<StockDataResponse> {
    return this.http.get<StockDataResponse>(`${this.apiUrl}/${symbol.toUpperCase()}/daily`);
  }

  /**
   * Récupère les informations fondamentales d'une entreprise.
   * 
   * @param symbol Symbole boursier
   * @returns Observable des métriques fondamentales
   */
  getCompanyOverview(symbol: string): Observable<CompanyOverview> {
    return this.http.get<CompanyOverview>(`${this.apiUrl}/${symbol.toUpperCase()}/overview`);
  }

  /**
   * Récupère l'indicateur RSI.
   * 
   * @param symbol Symbole boursier
   * @param interval Intervalle
   * @param timePeriod Période (défaut: 14)
   * @returns Observable du RSI
   */
  getRSI(symbol: string, interval: string = 'daily', timePeriod: number = 14): Observable<TechnicalIndicators> {
    const params = new HttpParams()
      .set('interval', interval)
      .set('time_period', timePeriod.toString());
    return this.http.get<TechnicalIndicators>(`${this.apiUrl}/${symbol.toUpperCase()}/rsi`, { params });
  }

  /**
   * Récupère l'indicateur MACD.
   * 
   * @param symbol Symbole boursier
   * @param interval Intervalle
   * @returns Observable du MACD
   */
  getMACD(symbol: string, interval: string = 'daily'): Observable<TechnicalIndicators> {
    const params = new HttpParams().set('interval', interval);
    return this.http.get<TechnicalIndicators>(`${this.apiUrl}/${symbol.toUpperCase()}/macd`, { params });
  }
}
