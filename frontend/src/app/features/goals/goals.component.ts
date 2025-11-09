import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { GoalService } from '@core/services/goal.service';
import { Goal, GoalPriority, GoalStatus } from '@core/models/goal.model';
import { GoalDialogComponent } from './goal-dialog/goal-dialog.component';

@Component({
  selector: 'app-goals',
  templateUrl: './goals.component.html',
  styleUrls: ['./goals.component.scss']
})
export class GoalsComponent implements OnInit {
  displayedColumns: string[] = ['name', 'target', 'current', 'progress', 'deadline', 'allocation', 'priority', 'status', 'actions'];
  dataSource: MatTableDataSource<Goal>;
  loading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private goalService: GoalService,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<Goal>([]);
  }

  ngOnInit(): void {
    this.loadGoals();
    this.setupFilter();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  setupFilter(): void {
    this.dataSource.filterPredicate = (data: Goal, filter: string) => {
      const searchStr = filter.toLowerCase();
      return (
        data.name.toLowerCase().includes(searchStr) ||
        (data.description && data.description.toLowerCase().includes(searchStr)) ||
        data.targetAmount.toString().includes(searchStr)
      );
    };

    this.dataSource.sortingDataAccessor = (item: Goal, property: string) => {
      switch (property) {
        case 'target':
          return item.targetAmount;
        case 'current':
          return item.currentAmount;
        case 'progress':
          return item.percentageAchieved;
        case 'deadline':
          return item.targetDate ? new Date(item.targetDate).getTime() : 0;
        case 'allocation':
          return item.allocationPercentage || 0;
        default:
          return (item as any)[property];
      }
    };
  }

  loadGoals(): void {
    this.loading = true;
    this.goalService.getAllGoals().subscribe({
      next: (goals) => {
        this.dataSource.data = goals;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des objectifs:', error);
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
    const dialogRef = this.dialog.open(GoalDialogComponent, {
      width: '600px',
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadGoals();
      }
    });
  }

  openEditDialog(goal: Goal): void {
    const dialogRef = this.dialog.open(GoalDialogComponent, {
      width: '600px',
      data: goal
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadGoals();
      }
    });
  }

  deleteGoal(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet objectif ?')) {
      this.goalService.deleteGoal(id).subscribe({
        next: () => {
          this.loadGoals();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
        }
      });
    }
  }

  getProgressColor(goal: Goal): string {
    if (goal.isCompleted) return 'accent';
    if (goal.isOverdue) return 'warn';
    if (goal.percentageAchieved >= 75) return 'primary';
    return 'primary';
  }

  getStatusLabel(goal: Goal): string {
    if (goal.isCompleted) return 'Complété';
    if (goal.isOverdue) return 'En retard';
    if (goal.status === GoalStatus.CANCELLED) return 'Annulé';
    return 'En cours';
  }

  getStatusClass(goal: Goal): string {
    if (goal.isCompleted) return 'completed';
    if (goal.isOverdue) return 'overdue';
    if (goal.status === GoalStatus.CANCELLED) return 'cancelled';
    return 'in-progress';
  }

  getPriorityLabel(priority: GoalPriority): string {
    switch (priority) {
      case GoalPriority.HIGH: return 'Haute';
      case GoalPriority.MEDIUM: return 'Moyenne';
      case GoalPriority.LOW: return 'Basse';
      default: return priority;
    }
  }

  getPriorityClass(priority: GoalPriority): string {
    return priority.toLowerCase();
  }
}
