import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Alert, AlertType, AlertSeverity } from '../../core/models/alert.model';
import { AlertService } from '../../core/services/alert.service';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss']
})
export class AlertsComponent implements OnInit {
  alerts: Alert[] = [];
  filteredAlerts: Alert[] = [];
  loading = false;
  filter: 'all' | 'unread' = 'all';
  unreadCount = 0;

  // Expose les enums au template
  AlertType = AlertType;
  AlertSeverity = AlertSeverity;

  constructor(
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAlerts();
    this.loadUnreadCount();
  }

  loadAlerts(): void {
    this.loading = true;
    this.alertService.getAllAlerts().subscribe({
      next: (data) => {
        this.alerts = data;
        this.applyFilter();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadUnreadCount(): void {
    this.alertService.getUnreadCount().subscribe({
      next: (data) => {
        this.unreadCount = data.count;
      }
    });
  }

  generateAlerts(): void {
    this.loading = true;
    this.alertService.generateAlerts().subscribe({
      next: () => {
        this.loadAlerts();
        this.loadUnreadCount();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  markAsRead(alert: Alert): void {
    if (alert.isRead) return;
    
    this.alertService.markAsRead(alert.id).subscribe({
      next: () => {
        alert.isRead = true;
        this.loadUnreadCount();
      }
    });
  }

  markAllAsRead(): void {
    this.alertService.markAllAsRead().subscribe({
      next: () => {
        this.alerts.forEach(alert => alert.isRead = true);
        this.applyFilter();
        this.loadUnreadCount();
      }
    });
  }

  deleteAlert(alert: Alert): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette alerte ?')) {
      return;
    }

    this.alertService.deleteAlert(alert.id).subscribe({
      next: () => {
        this.alerts = this.alerts.filter(a => a.id !== alert.id);
        this.applyFilter();
        this.loadUnreadCount();
      }
    });
  }

  navigateToBudget(budgetId: number): void {
    this.router.navigate(['/budgets'], { queryParams: { highlight: budgetId } });
  }

  navigateToGoal(goalId: number): void {
    this.router.navigate(['/goals'], { queryParams: { highlight: goalId } });
  }

  setFilter(filter: 'all' | 'unread'): void {
    this.filter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.filter === 'unread') {
      this.filteredAlerts = this.alerts.filter(alert => !alert.isRead);
    } else {
      this.filteredAlerts = this.alerts;
    }
  }

  getSeverityClass(severity: AlertSeverity): string {
    switch (severity) {
      case AlertSeverity.CRITICAL:
        return 'severity-critical';
      case AlertSeverity.WARNING:
        return 'severity-warning';
      case AlertSeverity.INFO:
        return 'severity-info';
      default:
        return '';
    }
  }

  getSeverityIcon(severity: AlertSeverity): string {
    switch (severity) {
      case AlertSeverity.CRITICAL:
        return 'error';
      case AlertSeverity.WARNING:
        return 'warning';
      case AlertSeverity.INFO:
        return 'info';
      default:
        return 'notifications';
    }
  }

  getTypeLabel(type: AlertType): string {
    switch (type) {
      case AlertType.BUDGET_EXCEEDED:
        return 'Budget dépassé';
      case AlertType.BUDGET_WARNING:
        return 'Alerte budget';
      case AlertType.GOAL_DEADLINE_APPROACHING:
        return 'Échéance objectif';
      case AlertType.UNUSUAL_SPENDING:
        return 'Dépense inhabituelle';
      default:
        return 'Alerte';
    }
  }

  getRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'À l\'instant';
    if (diffMins < 60) return `Il y a ${diffMins} min`;
    if (diffHours < 24) return `Il y a ${diffHours}h`;
    if (diffDays === 1) return 'Hier';
    if (diffDays < 7) return `Il y a ${diffDays}j`;
    
    return date.toLocaleDateString('fr-FR');
  }
}
