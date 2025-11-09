import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BudgetService } from '@core/services/budget.service';
import { CategoryService } from '@core/services/category.service';
import { Budget, BudgetRequest } from '@core/models/budget.model';
import { Category, TransactionType } from '@core/models/transaction.model';

@Component({
  selector: 'app-budget-dialog',
  templateUrl: './budget-dialog.component.html',
  styleUrls: ['./budget-dialog.component.scss']
})
export class BudgetDialogComponent implements OnInit {
  budgetForm: FormGroup;
  isEditMode = false;
  loading = false;
  categories: Category[] = [];

  constructor(
    private fb: FormBuilder,
    private budgetService: BudgetService,
    private categoryService: CategoryService,
    public dialogRef: MatDialogRef<BudgetDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Budget | null
  ) {
    this.isEditMode = !!data;
    
    this.budgetForm = this.fb.group({
      name: [data?.name || '', [Validators.required, Validators.minLength(2)]],
      amount: [data?.amount || '', [Validators.required, Validators.min(0.01)]],
      startDate: [data?.startDate || new Date().toISOString().split('T')[0], Validators.required],
      endDate: [data?.endDate || '', Validators.required],
      categoryId: [data?.category?.id || '', Validators.required],
      alertEnabled: [data?.alertEnabled !== undefined ? data.alertEnabled : true],
      alertThreshold: [data?.alertThreshold || 80, [Validators.min(0), Validators.max(100)]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    // Charger uniquement les catégories de type EXPENSE
    this.categoryService.getCategoriesByType(TransactionType.EXPENSE).subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: () => {}
    });
  }

  onSubmit(): void {
    if (this.budgetForm.valid) {
      this.loading = true;
      const request: BudgetRequest = this.budgetForm.value;

      const operation = this.isEditMode
        ? this.budgetService.updateBudget(this.data!.id, request)
        : this.budgetService.createBudget(request);

      operation.subscribe({
        next: () => this.dialogRef.close(true),
        error: () => {
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
