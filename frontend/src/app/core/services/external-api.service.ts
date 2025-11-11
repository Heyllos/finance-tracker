import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ExchangeRate, CryptoPrice } from '../models/exchange-rate.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ExternalApiService {
  private apiUrl = `${environment.apiUrl}/external`;

  constructor(private http: HttpClient) {}

  getExchangeRates(base: string = 'USD'): Observable<ExchangeRate> {
    return this.http.get<ExchangeRate>(`${this.apiUrl}/rates?base=${base}`);
  }

  convertCurrency(base: string, target: string): Observable<ExchangeRate> {
    return this.http.get<ExchangeRate>(`${this.apiUrl}/rates/convert?base=${base}&target=${target}`);
  }

  getCryptoPrices(): Observable<CryptoPrice[]> {
    return this.http.get<CryptoPrice[]>(`${this.apiUrl}/crypto`);
  }
}
