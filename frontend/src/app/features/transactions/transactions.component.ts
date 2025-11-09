import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TransactionService } from '@core/services/transaction.service';
import { Transaction, TransactionType } from '@core/models/transaction.model';
import { TransactionDialogComponent } from './transaction-dialog/transaction-dialog.component';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {
  displayedColumns: string[] = ['transactionDate', 'description', 'category', 'type', 'amount', 'merchant', 'actions'];
  dataSource: MatTableDataSource<Transaction>;
  loading = false;
  TransactionType = TransactionType;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private transactionService: TransactionService,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<Transaction>([]);
  }

  ngOnInit(): void {
    this.loadTransactions();
    this.setupFilter();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  setupFilter(): void {
    // Fonction de filtrage personnalisée pour gérer les objets imbriqués
    this.dataSource.filterPredicate = (data: Transaction, filter: string) => {
      const searchStr = filter.toLowerCase();
      
      return (
        data.description.toLowerCase().includes(searchStr) ||
        data.category.name.toLowerCase().includes(searchStr) ||
        (data.merchant && data.merchant.toLowerCase().includes(searchStr)) ||
        (data.notes && data.notes.toLowerCase().includes(searchStr)) ||
        data.amount.toString().includes(searchStr) ||
        this.getTypeLabel(data.type).toLowerCase().includes(searchStr) ||
        data.transactionDate.includes(searchStr)
      );
    };

    // Fonction de tri personnalisée pour gérer les objets imbriqués
    this.dataSource.sortingDataAccessor = (item: Transaction, property: string) => {
      switch (property) {
        case 'category':
          return item.category.name.toLowerCase();
        case 'type':
          return this.getTypeLabel(item.type).toLowerCase();
        case 'amount':
          return item.amount;
        case 'transactionDate':
          return new Date(item.transactionDate).getTime();
        case 'description':
          return item.description.toLowerCase();
        case 'merchant':
          return (item.merchant || '').toLowerCase();
        default:
          return (item as any)[property];
      }
    };
  }

  loadTransactions(): void {
    this.loading = true;
    this.transactionService.getAllTransactions().subscribe({
      next: (transactions) => {
        this.dataSource.data = transactions;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des transactions:', error);
        this.loading = false;
      }
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(TransactionDialogComponent, {
      width: '600px',
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTransactions();
      }
    });
  }

  openEditDialog(transaction: Transaction): void {
    const dialogRef = this.dialog.open(TransactionDialogComponent, {
      width: '600px',
      data: transaction
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTransactions();
      }
    });
  }

  deleteTransaction(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette transaction ?')) {
      this.transactionService.deleteTransaction(id).subscribe({
        next: () => {
          this.loadTransactions();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
        }
      });
    }
  }

  getTypeLabel(type: TransactionType): string {
    return type === TransactionType.INCOME ? 'Revenu' : 'Dépense';
  }

  getTypeClass(type: TransactionType): string {
    return type === TransactionType.INCOME ? 'income' : 'expense';
  }
}
