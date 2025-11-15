import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PortfolioService } from '../../core/services/portfolio.service';
import { PortfolioSummary, PortfolioPosition, StockTransactionType } from '../../core/models/portfolio.model';
import { StockTransactionDialogComponent } from './stock-transaction-dialog/stock-transaction-dialog.component';

@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.scss']
})
export class PortfolioComponent implements OnInit {
  summary: PortfolioSummary | null = null;
  positions: PortfolioPosition[] = [];
  loading = false;
  refreshingPrices = false;
  displayedColumns: string[] = ['symbol', 'quantity', 'averagePrice', 'currentPrice', 'value', 'gainLoss', 'actions'];

  constructor(
    private portfolioService: PortfolioService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadPortfolio();
  }

  loadPortfolio(): void {
    this.loading = true;
    this.portfolioService.getPortfolioSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.positions = data.positions;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du portefeuille:', error);
        this.loading = false;
      }
    });
  }

  openBuyDialog(): void {
    const dialogRef = this.dialog.open(StockTransactionDialogComponent, {
      width: '600px',
      data: { type: StockTransactionType.BUY }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadPortfolio();
      }
    });
  }

  openSellDialog(position: PortfolioPosition): void {
    const dialogRef = this.dialog.open(StockTransactionDialogComponent, {
      width: '600px',
      data: { 
        type: StockTransactionType.SELL,
        position: position
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadPortfolio();
      }
    });
  }

  refreshPrices(): void {
    this.refreshingPrices = true;
    this.portfolioService.refreshPrices().subscribe({
      next: () => {
        this.refreshingPrices = false;
        this.loadPortfolio();
      },
      error: (error) => {
        console.error('Erreur lors de l\'actualisation des prix:', error);
        this.refreshingPrices = false;
      }
    });
  }

  getGainLossClass(gainLoss: number | undefined): string {
    if (!gainLoss) return '';
    return gainLoss >= 0 ? 'positive' : 'negative';
  }

  getGainLossIcon(gainLoss: number | undefined): string {
    if (!gainLoss) return 'remove';
    return gainLoss >= 0 ? 'trending_up' : 'trending_down';
  }
}
