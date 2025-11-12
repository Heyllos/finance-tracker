import { Component, Input, OnInit } from '@angular/core';
import { ExternalApiService } from '../../../core/services/external-api.service';

interface Currency {
  code: string;
  name: string;
  flag?: string;
  icon?: string;
}

@Component({
  selector: 'app-currency-converter',
  templateUrl: './currency-converter.component.html',
  styleUrls: ['./currency-converter.component.scss']
})
export class CurrencyConverterComponent implements OnInit {
  @Input() type: 'forex' | 'crypto' = 'forex';
  
  isOpen = false;
  amount = 1;
  fromCurrency = 'USD';
  toCurrency = 'EUR';
  result = 0;
  loading = false;

  forexCurrencies: Currency[] = [
    { code: 'USD', name: 'Dollar américain', flag: '🇺🇸' },
    { code: 'EUR', name: 'Euro', flag: '🇪🇺' },
    { code: 'GBP', name: 'Livre sterling', flag: '🇬🇧' },
    { code: 'JPY', name: 'Yen japonais', flag: '🇯🇵' },
    { code: 'CHF', name: 'Franc suisse', flag: '🇨🇭' },
    { code: 'CAD', name: 'Dollar canadien', flag: '🇨🇦' },
    { code: 'AUD', name: 'Dollar australien', flag: '🇦🇺' },
    { code: 'CNY', name: 'Yuan chinois', flag: '🇨🇳' }
  ];

  cryptoCurrencies: Currency[] = [
    { code: 'BTC', name: 'Bitcoin', icon: '₿' },
    { code: 'ETH', name: 'Ethereum', icon: 'Ξ' },
    { code: 'BNB', name: 'Binance Coin', icon: 'BNB' },
    { code: 'ADA', name: 'Cardano', icon: '₳' },
    { code: 'SOL', name: 'Solana', icon: '◎' },
    { code: 'XRP', name: 'Ripple', icon: 'XRP' },
    { code: 'DOT', name: 'Polkadot', icon: '●' },
    { code: 'DOGE', name: 'Dogecoin', icon: 'Ð' },
    { code: 'USD', name: 'Dollar américain', icon: '$' },
    { code: 'EUR', name: 'Euro', icon: '€' }
  ];

  constructor(private externalApiService: ExternalApiService) {}

  ngOnInit(): void {
    if (this.type === 'crypto') {
      this.fromCurrency = 'BTC';
      this.toCurrency = 'USD';
    }
    this.convert();
  }

  togglePanel(): void {
    this.isOpen = !this.isOpen;
  }

  convert(): void {
    if (!this.amount || this.amount <= 0) {
      this.result = 0;
      return;
    }

    this.loading = true;

    if (this.type === 'forex') {
      this.convertForex();
    } else {
      this.convertCrypto();
    }
  }

  private convertForex(): void {
    this.externalApiService.getExchangeRates(this.fromCurrency).subscribe({
      next: (data) => {
        const rate = data.rates[this.toCurrency];
        if (rate) {
          this.result = this.amount * rate;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private convertCrypto(): void {
    // Pour crypto, on utilise une conversion simplifiée via les prix USD
    this.externalApiService.getCryptoPrices().subscribe({
      next: (prices) => {
        const fromPrice = this.getCryptoPrice(prices, this.fromCurrency);
        const toPrice = this.getCryptoPrice(prices, this.toCurrency);
        
        if (fromPrice && toPrice) {
          this.result = (this.amount * fromPrice) / toPrice;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private getCryptoPrice(prices: any[], symbol: string): number {
    if (symbol === 'USD') return 1;
    if (symbol === 'EUR') return 0.92; // Approximation
    
    const crypto = prices.find(p => p.symbol === symbol);
    return crypto ? crypto.priceUsd : 0;
  }

  swapCurrencies(): void {
    const temp = this.fromCurrency;
    this.fromCurrency = this.toCurrency;
    this.toCurrency = temp;
    this.convert();
  }

  get currencies(): Currency[] {
    return this.type === 'forex' ? this.forexCurrencies : this.cryptoCurrencies;
  }

  getCurrencyDisplay(code: string): string {
    const currency = this.currencies.find(c => c.code === code);
    if (!currency) return code;
    
    if (this.type === 'forex') {
      return `${currency.flag || ''} ${code}`;
    } else {
      return `${currency.icon || ''} ${code}`;
    }
  }
}
