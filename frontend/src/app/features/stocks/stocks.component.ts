import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { CandlestickController, CandlestickElement } from 'chartjs-chart-financial';
import { StockService } from '../../core/services/stock.service';
import { StockDataResponse, StockDataPoint, StockInterval, CompanyOverview, TechnicalIndicators, ChartType } from '../../core/models/stock.model';
import { forkJoin } from 'rxjs';

// Enregistrer tous les composants Chart.js
Chart.register(...registerables, CandlestickController, CandlestickElement);

@Component({
  selector: 'app-stocks',
  templateUrl: './stocks.component.html',
  styleUrls: ['./stocks.component.scss']
})
export class StocksComponent implements OnInit, AfterViewInit {
  @ViewChild('chartCanvas', { static: false }) chartCanvas!: ElementRef<HTMLCanvasElement>;

  // Données du formulaire
  symbol = 'AAPL';
  selectedInterval: StockInterval = 'daily';
  startDate: string = '';
  endDate: string = '';
  
  // Données boursières
  stockData: StockDataResponse | null = null;
  companyOverview: CompanyOverview | null = null;
  rsiData: TechnicalIndicators | null = null;
  macdData: TechnicalIndicators | null = null;
  
  loading = false;
  loadingOverview = false;
  loadingIndicators = false;
  loadingRSI = false;
  loadingMACD = false;
  error: string | null = null;

  // Graphique
  chart: Chart | null = null;
  chartType: ChartType = 'line';
  
  // Favoris
  favorites: string[] = [];
  
  // Onglet actif
  activeTab = 0;

  // Options d'intervalle
  intervals: { value: StockInterval; label: string }[] = [
    { value: '1min', label: '1 minute' },
    { value: '5min', label: '5 minutes' },
    { value: '15min', label: '15 minutes' },
    { value: '30min', label: '30 minutes' },
    { value: '60min', label: '1 heure' },
    { value: 'daily', label: 'Quotidien' }
  ];

  // Symboles populaires
  popularSymbols = [
    { symbol: 'AAPL', name: 'Apple' },
    { symbol: 'TSLA', name: 'Tesla' },
    { symbol: 'MSFT', name: 'Microsoft' },
    { symbol: 'GOOGL', name: 'Google' },
    { symbol: 'AMZN', name: 'Amazon' },
    { symbol: 'META', name: 'Meta' },
    { symbol: 'NVDA', name: 'NVIDIA' },
    { symbol: 'AMD', name: 'AMD' }
  ];

  constructor(private stockService: StockService) {}

  ngOnInit(): void {
    // Charger les favoris depuis localStorage
    const savedFavorites = localStorage.getItem('stockFavorites');
    if (savedFavorites) {
      this.favorites = JSON.parse(savedFavorites);
    }
    
    // Chargement initial avec AAPL
    this.loadAllData();
  }

  ngAfterViewInit(): void {
    // Le graphique sera créé après le chargement des données
  }

  /**
   * Charge toutes les données (prix + métriques + indicateurs).
   */
  loadAllData(): void {
    this.loadStockData();
    this.loadCompanyOverview();
    this.loadTechnicalIndicators();
  }

  /**
   * Charge les données boursières.
   */
  loadStockData(): void {
    if (!this.symbol || this.symbol.trim() === '') {
      this.error = 'Veuillez entrer un symbole boursier';
      return;
    }

    this.loading = true;
    this.error = null;

    const request = this.selectedInterval === 'daily'
      ? this.stockService.getDailyStockData(this.symbol)
      : this.stockService.getStockData(this.symbol, this.selectedInterval);

    request.subscribe({
      next: (data) => {
        this.stockData = data;
        this.loading = false;
        // Attendre un tick pour que le canvas soit rendu
        setTimeout(() => this.createChart(), 0);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Erreur lors du chargement des données';
        console.error('Erreur:', err);
      }
    });
  }

  /**
   * Charge les métriques fondamentales.
   */
  loadCompanyOverview(): void {
    if (!this.symbol || this.symbol.trim() === '') {
      return;
    }

    this.loadingOverview = true;
    this.stockService.getCompanyOverview(this.symbol).subscribe({
      next: (data) => {
        this.companyOverview = data;
        this.loadingOverview = false;
      },
      error: (err) => {
        this.loadingOverview = false;
        console.error('Erreur chargement overview:', err);
      }
    });
  }

  /**
   * Charge uniquement le RSI.
   */
  loadRSI(): void {
    if (!this.symbol || this.symbol.trim() === '') {
      return;
    }

    this.loadingRSI = true;
    this.rsiData = null;
    
    this.stockService.getRSI(this.symbol, 'daily', 14).subscribe({
      next: (data) => {
        console.log('RSI reçu:', data);
        this.rsiData = data;
        this.loadingRSI = false;
      },
      error: (err) => {
        this.loadingRSI = false;
        console.error('Erreur chargement RSI:', err);
      }
    });
  }

  /**
   * Charge uniquement le MACD.
   */
  loadMACD(): void {
    if (!this.symbol || this.symbol.trim() === '') {
      return;
    }

    this.loadingMACD = true;
    this.macdData = null;
    
    this.stockService.getMACD(this.symbol, 'daily').subscribe({
      next: (data) => {
        console.log('MACD reçu:', data);
        this.macdData = data;
        this.loadingMACD = false;
      },
      error: (err) => {
        this.loadingMACD = false;
        console.error('Erreur chargement MACD:', err);
      }
    });
  }

  /**
   * Charge les indicateurs techniques (DEPRECATED - utiliser loadRSI et loadMACD).
   */
  loadTechnicalIndicators(): void {
    if (!this.symbol || this.symbol.trim() === '') {
      return;
    }

    this.loadingIndicators = true;
    this.rsiData = null;
    this.macdData = null;
    
    forkJoin({
      rsi: this.stockService.getRSI(this.symbol, 'daily', 14),
      macd: this.stockService.getMACD(this.symbol, 'daily')
    }).subscribe({
      next: (data) => {
        console.log('Indicateurs techniques reçus:', data);
        this.rsiData = data.rsi;
        this.macdData = data.macd;
        this.loadingIndicators = false;
      },
      error: (err) => {
        this.loadingIndicators = false;
        console.error('Erreur chargement indicateurs:', err);
        console.error('Détails erreur:', err.error);
        // Ne pas afficher d'erreur à l'utilisateur, juste ne pas afficher les indicateurs
      }
    });
  }

  /**
   * Sélectionne un symbole populaire.
   */
  selectSymbol(symbol: string): void {
    this.symbol = symbol;
    this.loadAllData();
  }

  /**
   * Ajoute/retire des favoris.
   */
  toggleFavorite(): void {
    const index = this.favorites.indexOf(this.symbol.toUpperCase());
    if (index > -1) {
      this.favorites.splice(index, 1);
    } else {
      this.favorites.push(this.symbol.toUpperCase());
    }
    localStorage.setItem('stockFavorites', JSON.stringify(this.favorites));
  }

  /**
   * Vérifie si le symbole est en favori.
   */
  isFavorite(): boolean {
    return this.favorites.includes(this.symbol.toUpperCase());
  }

  /**
   * Change le type de graphique.
   */
  changeChartType(type: ChartType): void {
    this.chartType = type;
    this.createChart();
  }

  /**
   * Retourne le signal RSI.
   */
  getRSISignal(): { text: string; class: string } {
    if (!this.rsiData || !this.rsiData.rsi) {
      return { text: 'N/A', class: 'neutral' };
    }
    
    const rsi = this.rsiData.rsi;
    if (rsi > 70) {
      return { text: 'SURACHAT', class: 'negative' };
    } else if (rsi < 30) {
      return { text: 'SURVENTE', class: 'positive' };
    } else {
      return { text: 'NEUTRE', class: 'neutral' };
    }
  }

  /**
   * Retourne le signal MACD.
   */
  getMACDSignal(): { text: string; class: string } {
    if (!this.macdData || !this.macdData.macd || !this.macdData.macdSignal) {
      return { text: 'N/A', class: 'neutral' };
    }
    
    const diff = this.macdData.macd - this.macdData.macdSignal;
    if (diff > 0) {
      return { text: 'ACHAT', class: 'positive' };
    } else if (diff < 0) {
      return { text: 'VENTE', class: 'negative' };
    } else {
      return { text: 'NEUTRE', class: 'neutral' };
    }
  }

  /**
   * Formatte les grands nombres.
   */
  formatLargeNumber(value: number | null | undefined): string {
    if (!value) return 'N/A';
    
    if (value >= 1e12) {
      return `$${(value / 1e12).toFixed(2)}T`;
    } else if (value >= 1e9) {
      return `$${(value / 1e9).toFixed(2)}B`;
    } else if (value >= 1e6) {
      return `$${(value / 1e6).toFixed(2)}M`;
    } else {
      return `$${value.toFixed(2)}`;
    }
  }

  /**
   * Formatte les pourcentages.
   */
  formatPercentage(value: number | null | undefined): string {
    if (!value) return 'N/A';
    return `${(value * 100).toFixed(2)}%`;
  }

  /**
   * Crée ou met à jour le graphique Chart.js.
   */
  createChart(): void {
    if (!this.stockData || !this.chartCanvas) {
      console.log('Pas de données ou canvas pour le graphique');
      return;
    }

    // Détruire le graphique existant
    if (this.chart) {
      this.chart.destroy();
    }

    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) {
      return;
    }

    // Préparer les données
    const labels = this.stockData.timeSeries.map(point => 
      new Date(point.timestamp).toLocaleString('fr-FR', {
        month: 'short',
        day: 'numeric',
        hour: this.selectedInterval !== 'daily' ? '2-digit' : undefined,
        minute: this.selectedInterval !== 'daily' ? '2-digit' : undefined
      })
    );

    const closePrices = this.stockData.timeSeries.map(point => point.close);
    const openPrices = this.stockData.timeSeries.map(point => point.open);
    const highPrices = this.stockData.timeSeries.map(point => point.high);
    const lowPrices = this.stockData.timeSeries.map(point => point.low);

    let datasets: any[] = [];
    let chartType: any = 'line';

    // Configuration selon le type de graphique
    if (this.chartType === 'line') {
      chartType = 'line';
      datasets = [
        {
          label: 'Prix de clôture',
          data: closePrices,
          borderColor: '#2196f3',
          backgroundColor: 'transparent',
          borderWidth: 2,
          fill: false,
          tension: 0.1,
          pointRadius: 2,
          pointHoverRadius: 5
        },
        {
          label: 'Prix haut',
          data: highPrices,
          borderColor: '#4caf50',
          backgroundColor: 'transparent',
          borderWidth: 1,
          borderDash: [5, 5],
          fill: false,
          tension: 0.1,
          pointRadius: 0
        },
        {
          label: 'Prix bas',
          data: lowPrices,
          borderColor: '#f44336',
          backgroundColor: 'transparent',
          borderWidth: 1,
          borderDash: [5, 5],
          fill: false,
          tension: 0.1,
          pointRadius: 0
        }
      ];
    } else if (this.chartType === 'area') {
      chartType = 'line';
      datasets = [
        {
          label: 'Prix de clôture',
          data: closePrices,
          borderColor: '#2196f3',
          backgroundColor: 'rgba(33, 150, 243, 0.2)',
          borderWidth: 2,
          fill: true,
          tension: 0.4,
          pointRadius: 0,
          pointHoverRadius: 5
        }
      ];
    } else if (this.chartType === 'candlestick') {
      // Utilisation du vrai graphique candlestick avec chartjs-chart-financial
      chartType = 'candlestick';
      datasets = [
        {
          label: this.stockData.symbol,
          data: this.stockData.timeSeries.map((point, index) => ({
            x: labels[index],
            o: point.open,
            h: point.high,
            l: point.low,
            c: point.close
          })),
          color: {
            up: '#4caf50',
            down: '#f44336',
            unchanged: '#999'
          },
          borderColor: {
            up: '#4caf50',
            down: '#f44336',
            unchanged: '#999'
          }
        }
      ];
    }

    // Configuration du graphique
    const config: ChartConfiguration = {
      type: chartType,
      data: {
        labels: labels,
        datasets: datasets
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'top'
          },
          title: {
            display: true,
            text: `${this.stockData.symbol} - ${this.getIntervalLabel()}`,
            font: {
              size: 16,
              weight: 'bold'
            }
          },
          tooltip: {
            mode: 'index',
            intersect: false,
            callbacks: {
              label: (context) => {
                const value = context.parsed.y;
                return `${context.dataset.label}: $${value !== null ? value.toFixed(2) : 'N/A'}`;
              }
            }
          }
        },
        scales: {
          x: {
            display: true,
            title: {
              display: true,
              text: 'Date/Heure'
            },
            ticks: {
              maxRotation: 45,
              minRotation: 45
            }
          },
          y: {
            display: true,
            title: {
              display: true,
              text: 'Prix (USD)'
            },
            ticks: {
              callback: (value) => `$${value}`
            }
          }
        },
        interaction: {
          mode: 'nearest',
          axis: 'x',
          intersect: false
        }
      }
    };

    this.chart = new Chart(ctx, config);
  }

  /**
   * Retourne le label de l'intervalle sélectionné.
   */
  getIntervalLabel(): string {
    const interval = this.intervals.find(i => i.value === this.selectedInterval);
    return interval ? interval.label : this.selectedInterval;
  }

  /**
   * Calcule la variation en pourcentage.
   */
  getPriceChange(): { value: number; percentage: number } | null {
    if (!this.stockData || this.stockData.timeSeries.length < 2) {
      return null;
    }

    const latest = this.stockData.timeSeries[this.stockData.timeSeries.length - 1];
    const previous = this.stockData.timeSeries[this.stockData.timeSeries.length - 2];

    const change = latest.close - previous.close;
    const percentage = (change / previous.close) * 100;

    return { value: change, percentage };
  }

  /**
   * Retourne la classe CSS pour la variation.
   */
  getChangeClass(): string {
    const change = this.getPriceChange();
    if (!change) return '';
    return change.value >= 0 ? 'positive' : 'negative';
  }
}
