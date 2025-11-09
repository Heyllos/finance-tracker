import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TransactionService } from '@core/services/transaction.service';
import { CategoryService } from '@core/services/category.service';
import { Transaction, TransactionRequest, TransactionType, Category } from '@core/models/transaction.model';

@Component({
  selector: 'app-transaction-dialog',
  templateUrl: './transaction-dialog.component.html',
  styleUrls: ['./transaction-dialog.component.scss']
})
export class TransactionDialogComponent implements OnInit {
  transactionForm: FormGroup;
  isEditMode = false;
  loading = false;
  categories: Category[] = [];
  TransactionType = TransactionType;

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private categoryService: CategoryService,
    public dialogRef: MatDialogRef<TransactionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Transaction | null
  ) {
    this.isEditMode = !!data;
    
    this.transactionForm = this.fb.group({
      amount: [data?.amount || '', [Validators.required, Validators.min(0.01)]],
      description: [data?.description || '', [Validators.required, Validators.minLength(2)]],
      notes: [data?.notes || ''],
      transactionDate: [data?.transactionDate || new Date().toISOString().split('T')[0], Validators.required],
      type: [data?.type || TransactionType.EXPENSE, Validators.required],
      categoryId: [data?.category?.id || '', Validators.required],
      merchant: [data?.merchant || ''],
      paymentMethod: [data?.paymentMethod || '']
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    
    // Recharger les catégories quand le type change
    this.transactionForm.get('type')?.valueChanges.subscribe(() => {
      this.loadCategories();
      this.transactionForm.patchValue({ categoryId: '' });
    });
  }

  loadCategories(): void {
    const type = this.transactionForm.get('type')?.value;
    if (type) {
      this.categoryService.getCategoriesByType(type).subscribe({
        next: (categories) => {
          this.categories = categories;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des catégories:', error);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.transactionForm.valid) {
      this.loading = true;
      const request: TransactionRequest = this.transactionForm.value;

      const operation = this.isEditMode
        ? this.transactionService.updateTransaction(this.data!.id, request)
        : this.transactionService.createTransaction(request);

      operation.subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          console.error('Erreur:', error);
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
