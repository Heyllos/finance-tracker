import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { BudgetService } from '@core/services/budget.service';
import { Budget } from '@core/models/budget.model';
import { BudgetDialogComponent } from './budget-dialog/budget-dialog.component';

@Component({
  selector: 'app-budgets',
  templateUrl: './budgets.component.html',
  styleUrls: ['./budgets.component.scss']
})
export class BudgetsComponent implements OnInit {
  displayedColumns: string[] = ['name', 'category', 'period', 'amount', 'spent', 'progress', 'status', 'actions'];
  dataSource: MatTableDataSource<Budget>;
  loading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private budgetService: BudgetService,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<Budget>([]);
  }

  ngOnInit(): void {
    this.loadBudgets();
    this.setupFilter();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  setupFilter(): void {
    this.dataSource.filterPredicate = (data: Budget, filter: string) => {
      const searchStr = filter.toLowerCase();
      return (
        data.name.toLowerCase().includes(searchStr) ||
        data.category.name.toLowerCase().includes(searchStr) ||
        data.amount.toString().includes(searchStr)
      );
    };

    this.dataSource.sortingDataAccessor = (item: Budget, property: string) => {
      switch (property) {
        case 'category':
          return item.category.name.toLowerCase();
        case 'amount':
          return item.amount;
        case 'spent':
          return item.spentAmount;
        case 'progress':
          return item.percentageUsed;
        case 'period':
          return new Date(item.startDate).getTime();
        default:
          return (item as any)[property];
      }
    };
  }

  loadBudgets(): void {
    this.loading = true;
    this.budgetService.getAllBudgets().subscribe({
      next: (budgets) => {
        this.dataSource.data = budgets;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des budgets:', error);
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
    const dialogRef = this.dialog.open(BudgetDialogComponent, {
      width: '600px',
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBudgets();
      }
    });
  }

  openEditDialog(budget: Budget): void {
    const dialogRef = this.dialog.open(BudgetDialogComponent, {
      width: '600px',
      data: budget
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBudgets();
      }
    });
  }

  deleteBudget(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce budget ?')) {
      this.budgetService.deleteBudget(id).subscribe({
        next: () => {
          this.loadBudgets();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
        }
      });
    }
  }

  getProgressColor(budget: Budget): string {
    if (budget.isOverBudget) return 'warn';
    if (budget.shouldAlert) return 'accent';
    return 'primary';
  }

  getStatusLabel(budget: Budget): string {
    if (budget.isOverBudget) return 'Dépassé';
    if (budget.shouldAlert) return 'Alerte';
    if (budget.isActive) return 'Actif';
    return 'Inactif';
  }

  getStatusClass(budget: Budget): string {
    if (budget.isOverBudget) return 'over-budget';
    if (budget.shouldAlert) return 'alert';
    if (budget.isActive) return 'active';
    return 'inactive';
  }
}
