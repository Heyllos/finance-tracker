import { Component, OnInit } from '@angular/core';
import { CryptoPrice } from '../../core/models/exchange-rate.model';
import { ExternalApiService } from '../../core/services/external-api.service';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.scss']
})
export class CryptoComponent implements OnInit {
  cryptoPrices: CryptoPrice[] = [];
  loading = false;
  displayedColumns: string[] = ['symbol', 'name', 'priceUsd', 'priceEur', 'change24h'];

  constructor(private externalApiService: ExternalApiService) {}

  ngOnInit(): void {
    this.loadCryptoPrices();
  }

  loadCryptoPrices(): void {
    this.loading = true;
    this.externalApiService.getCryptoPrices().subscribe({
      next: (data) => {
        this.cryptoPrices = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  refresh(): void {
    this.loadCryptoPrices();
  }

  getCryptoIcon(symbol: string): string {
    const icons: { [key: string]: string } = {
      'BTC': '₿',
      'ETH': 'Ξ',
      'BNB': 'BNB',
      'ADA': '₳',
      'SOL': '◎',
      'XRP': 'XRP',
      'DOT': '●',
      'DOGE': 'Ð',
      'AVAX': 'AVAX',
      'LINK': 'LINK'
    };
    return icons[symbol] || symbol;
  }

  getChangeClass(change: number | undefined): string {
    if (!change) return '';
    return change >= 0 ? 'positive' : 'negative';
  }

  getChangeIcon(change: number | undefined): string {
    if (!change) return 'remove';
    return change >= 0 ? 'trending_up' : 'trending_down';
  }
}
