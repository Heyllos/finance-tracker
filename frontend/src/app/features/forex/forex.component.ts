import { Component, OnInit } from '@angular/core';
import { ExchangeRate } from '../../core/models/exchange-rate.model';
import { ExternalApiService } from '../../core/services/external-api.service';

@Component({
  selector: 'app-forex',
  templateUrl: './forex.component.html',
  styleUrls: ['./forex.component.scss']
})
export class ForexComponent implements OnInit {
  exchangeRates: ExchangeRate | null = null;
  loading = false;
  selectedBase = 'USD';
  
  availableCurrencies = ['USD', 'EUR', 'GBP', 'JPY', 'CHF', 'CAD', 'AUD', 'CNY'];
  displayedCurrencies: { code: string; rate: number; name: string }[] = [];

  currencyNames: { [key: string]: string } = {
    'USD': 'Dollar américain',
    'EUR': 'Euro',
    'GBP': 'Livre sterling',
    'JPY': 'Yen japonais',
    'CHF': 'Franc suisse',
    'CAD': 'Dollar canadien',
    'AUD': 'Dollar australien',
    'CNY': 'Yuan chinois',
    'INR': 'Roupie indienne',
    'BRL': 'Real brésilien',
    'RUB': 'Rouble russe',
    'KRW': 'Won sud-coréen',
    'MXN': 'Peso mexicain',
    'SGD': 'Dollar de Singapour',
    'HKD': 'Dollar de Hong Kong',
    'NOK': 'Couronne norvégienne',
    'SEK': 'Couronne suédoise',
    'DKK': 'Couronne danoise',
    'PLN': 'Zloty polonais',
    'THB': 'Baht thaïlandais',
    'IDR': 'Roupie indonésienne',
    'HUF': 'Forint hongrois',
    'CZK': 'Couronne tchèque',
    'ILS': 'Shekel israélien',
    'CLP': 'Peso chilien',
    'PHP': 'Peso philippin',
    'AED': 'Dirham des Émirats',
    'SAR': 'Riyal saoudien',
    'MYR': 'Ringgit malaisien',
    'ZAR': 'Rand sud-africain'
  };

  constructor(private externalApiService: ExternalApiService) {}

  ngOnInit(): void {
    this.loadExchangeRates();
  }

  loadExchangeRates(): void {
    this.loading = true;
    this.externalApiService.getExchangeRates(this.selectedBase).subscribe({
      next: (data) => {
        this.exchangeRates = data;
        this.prepareDisplayData();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  prepareDisplayData(): void {
    if (!this.exchangeRates) return;

    this.displayedCurrencies = Object.entries(this.exchangeRates.rates)
      .filter(([code]) => code !== this.selectedBase)
      .map(([code, rate]) => ({
        code,
        rate,
        name: this.currencyNames[code] || code
      }))
      .sort((a, b) => a.code.localeCompare(b.code));
  }

  onBaseChange(): void {
    this.loadExchangeRates();
  }

  refresh(): void {
    this.loadExchangeRates();
  }

  getFlag(currencyCode: string): string {
    const flags: { [key: string]: string } = {
      'USD': '🇺🇸', 'EUR': '🇪🇺', 'GBP': '🇬🇧', 'JPY': '🇯🇵',
      'CHF': '🇨🇭', 'CAD': '🇨🇦', 'AUD': '🇦🇺', 'CNY': '🇨🇳',
      'INR': '🇮🇳', 'BRL': '🇧🇷', 'RUB': '🇷🇺', 'KRW': '🇰🇷',
      'MXN': '🇲🇽', 'SGD': '🇸🇬', 'HKD': '🇭🇰', 'NOK': '🇳🇴',
      'SEK': '🇸🇪', 'DKK': '🇩🇰', 'PLN': '🇵🇱', 'THB': '🇹🇭'
    };
    return flags[currencyCode] || '🏳️';
  }
}
