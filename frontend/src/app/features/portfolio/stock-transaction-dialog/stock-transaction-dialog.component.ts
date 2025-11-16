import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PortfolioService } from '../../../core/services/portfolio.service';
import { StockService } from '../../../core/services/stock.service';
import { StockTransactionType, PortfolioPosition } from '../../../core/models/portfolio.model';

@Component({
  selector: 'app-stock-transaction-dialog',
  templateUrl: './stock-transaction-dialog.component.html',
  styleUrls: ['./stock-transaction-dialog.component.scss']
})
export class StockTransactionDialogComponent implements OnInit {
  transactionForm: FormGroup;
  type: StockTransactionType;
  position?: PortfolioPosition;
  loading = false;
  searchingStock = false;
  StockTransactionType = StockTransactionType;

  constructor(
    private fb: FormBuilder,
    private portfolioService: PortfolioService,
    private stockService: StockService,
    public dialogRef: MatDialogRef<StockTransactionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: StockTransactionType; position?: PortfolioPosition }
  ) {
    this.type = data.type;
    this.position = data.position;

    this.transactionForm = this.fb.group({
      symbol: [data.position?.symbol || '', [Validators.required, Validators.maxLength(10)]],
      companyName: [data.position?.companyName || '', [Validators.required, Validators.maxLength(100)]],
      quantity: [1, [Validators.required, Validators.min(1)]],
      pricePerShare: ['', [Validators.required, Validators.min(0.01)]],
      fees: [0, [Validators.min(0)]],
      transactionDate: [new Date().toISOString(), Validators.required],
      notes: ['', Validators.maxLength(500)]
    });

    if (data.position) {
      this.transactionForm.patchValue({
        symbol: data.position.symbol,
        companyName: data.position.companyName
      });
      this.transactionForm.get('symbol')?.disable();
      this.transactionForm.get('companyName')?.disable();
    }
  }

  ngOnInit(): void {
    if (this.position && this.position.currentPrice) {
      this.transactionForm.patchValue({
        pricePerShare: this.position.currentPrice
      });
    }
  }

  searchStock(): void {
    const symbol = this.transactionForm.get('symbol')?.value;
    if (!symbol || symbol.length < 1) return;

    this.searchingStock = true;
    this.stockService.getDailyStockData(symbol.toUpperCase()).subscribe({
      next: (data) => {
        if (data && data.timeSeries && data.timeSeries.length > 0) {
          const latestPrice = data.timeSeries[0].close;
          this.transactionForm.patchValue({
            pricePerShare: latestPrice,
            companyName: symbol.toUpperCase()
          });
        }
        this.searchingStock = false;
      },
      error: () => {
        this.searchingStock = false;
      }
    });
  }

  calculateTotal(): number {
    const quantity = this.transactionForm.get('quantity')?.value || 0;
    const pricePerShare = this.transactionForm.get('pricePerShare')?.value || 0;
    const fees = this.transactionForm.get('fees')?.value || 0;
    return (quantity * pricePerShare) + fees;
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) return;

    this.loading = true;
    const formValue = this.transactionForm.getRawValue();

    const request = {
      symbol: formValue.symbol.toUpperCase(),
      companyName: formValue.companyName,
      type: this.type,
      quantity: formValue.quantity,
      pricePerShare: formValue.pricePerShare,
      fees: formValue.fees || 0,
      transactionDate: formValue.transactionDate,
      notes: formValue.notes
    };

    this.portfolioService.createTransaction(request).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Erreur lors de la création de la transaction:', error);
        this.loading = false;
        alert(error.error?.message || 'Une erreur est survenue');
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  get dialogTitle(): string {
    return this.type === StockTransactionType.BUY ? 'Acheter des actions' : 'Vendre des actions';
  }

  get submitButtonText(): string {
    return this.type === StockTransactionType.BUY ? 'Acheter' : 'Vendre';
  }

  get maxQuantity(): number | null {
    return this.type === StockTransactionType.SELL && this.position 
      ? this.position.totalQuantity 
      : null;
  }
}
