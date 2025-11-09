import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { GoalService } from '@core/services/goal.service';
import { Goal, GoalRequest, GoalPriority } from '@core/models/goal.model';

@Component({
  selector: 'app-goal-dialog',
  templateUrl: './goal-dialog.component.html',
  styleUrls: ['./goal-dialog.component.scss']
})
export class GoalDialogComponent implements OnInit {
  goalForm: FormGroup;
  isEditMode = false;
  loading = false;
  priorities = [
    { value: GoalPriority.LOW, label: 'Basse' },
    { value: GoalPriority.MEDIUM, label: 'Moyenne' },
    { value: GoalPriority.HIGH, label: 'Haute' }
  ];

  constructor(
    private fb: FormBuilder,
    private goalService: GoalService,
    public dialogRef: MatDialogRef<GoalDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Goal | null
  ) {
    this.isEditMode = !!data;
    
    this.goalForm = this.fb.group({
      name: [data?.name || '', [Validators.required, Validators.minLength(2)]],
      description: [data?.description || ''],
      targetAmount: [data?.targetAmount || '', [Validators.required, Validators.min(0.01)]],
      targetDate: [data?.targetDate || ''],
      priority: [data?.priority || GoalPriority.MEDIUM, Validators.required],
      allocationPercentage: [data?.allocationPercentage || '', [Validators.min(0), Validators.max(100)]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.goalForm.valid) {
      this.loading = true;
      const request: GoalRequest = this.goalForm.value;

      const operation = this.isEditMode
        ? this.goalService.updateGoal(this.data!.id, request)
        : this.goalService.createGoal(request);

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
