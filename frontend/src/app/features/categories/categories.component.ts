import { Component, OnInit } from '@angular/core';
import { CategoryService } from '@core/services/category.service';
import { Category, TransactionType } from '@core/models/transaction.model';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss']
})
export class CategoriesComponent implements OnInit {
  allCategories: Category[] = [];
  incomeCategories: Category[] = [];
  expenseCategories: Category[] = [];
  loading = false;
  selectedTab = 'all'; // all, income, expense

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.allCategories = categories;
        this.incomeCategories = categories.filter(c => c.type === TransactionType.INCOME);
        this.expenseCategories = categories.filter(c => c.type === TransactionType.EXPENSE);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  selectTab(tab: string): void {
    this.selectedTab = tab;
  }

  getDisplayedCategories(): Category[] {
    switch (this.selectedTab) {
      case 'income':
        return this.incomeCategories;
      case 'expense':
        return this.expenseCategories;
      default:
        return this.allCategories;
    }
  }

  getTypeLabel(type: TransactionType): string {
    return type === TransactionType.INCOME ? 'Revenu' : 'Dépense';
  }

  getTypeIcon(type: TransactionType): string {
    return type === TransactionType.INCOME ? 'arrow_upward' : 'arrow_downward';
  }

  getTypeClass(type: TransactionType): string {
    return type === TransactionType.INCOME ? 'income' : 'expense';
  }
}
