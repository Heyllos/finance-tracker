import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  StockTransaction, 
  StockTransactionRequest, 
  PortfolioPosition, 
  PortfolioSummary 
} from '../models/portfolio.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {
  private apiUrl = `${environment.apiUrl}/portfolio`;

  constructor(private http: HttpClient) {}

  /**
   * Créer une nouvelle transaction (achat ou vente).
   */
  createTransaction(request: StockTransactionRequest): Observable<StockTransaction> {
    return this.http.post<StockTransaction>(`${this.apiUrl}/transactions`, request);
  }

  /**
   * Obtenir toutes les positions du portefeuille.
   */
  getPortfolio(): Observable<PortfolioPosition[]> {
    return this.http.get<PortfolioPosition[]>(`${this.apiUrl}/positions`);
  }

  /**
   * Obtenir le résumé du portefeuille avec statistiques.
   */
  getPortfolioSummary(): Observable<PortfolioSummary> {
    return this.http.get<PortfolioSummary>(`${this.apiUrl}/summary`);
  }

  /**
   * Obtenir l'historique de toutes les transactions.
   */
  getTransactionHistory(): Observable<StockTransaction[]> {
    return this.http.get<StockTransaction[]>(`${this.apiUrl}/transactions`);
  }

  /**
   * Obtenir une position spécifique par symbole.
   */
  getPosition(symbol: string): Observable<PortfolioPosition> {
    return this.http.get<PortfolioPosition>(`${this.apiUrl}/positions/${symbol}`);
  }

  /**
   * Obtenir les transactions pour un symbole spécifique.
   */
  getTransactionsBySymbol(symbol: string): Observable<StockTransaction[]> {
    return this.http.get<StockTransaction[]>(`${this.apiUrl}/transactions/${symbol}`);
  }

  /**
   * Actualiser les prix actuels de toutes les positions.
   */
  refreshPrices(): Observable<any> {
    return this.http.put(`${this.apiUrl}/refresh-prices`, {});
  }
}
