import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { AuthService } from '@core/services/auth.service';
import { User } from '@core/models/auth.model';
import { StatisticsService, DashboardStats, CategoryExpense, MonthlyData, BudgetProgress, GoalProjection, TopExpense } from '@core/services/statistics.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  currentUser: User | null = null;
  loading = true;
  
  // Stats
  stats: DashboardStats | null = null;
  
  // Charts
  @ViewChild('trendsChart') trendsChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('categoryChart') categoryChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('budgetChart') budgetChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('goalsChart') goalsChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('topExpensesChart') topExpensesChartRef!: ElementRef<HTMLCanvasElement>;
  
  private trendsChart: Chart | null = null;
  private categoryChart: Chart | null = null;
  private budgetChart: Chart | null = null;
  private goalsChart: Chart | null = null;
  private topExpensesChart: Chart | null = null;

  constructor(
    private authService: AuthService,
    private statisticsService: StatisticsService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    this.loadStats();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.loadCharts();
    }, 100);
  }

  loadStats(): void {
    this.statisticsService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadCharts(): void {
    this.loadTrendsChart();
    this.loadCategoryChart();
    this.loadBudgetChart();
    this.loadGoalsChart();
    this.loadTopExpensesChart();
  }

  loadTrendsChart(): void {
    this.statisticsService.getMonthlyTrends(6).subscribe({
      next: (data: MonthlyData[]) => {
        const ctx = this.trendsChartRef.nativeElement.getContext('2d');
        if (ctx) {
          this.trendsChart = new Chart(ctx, {
            type: 'line',
            data: {
              labels: data.map(d => d.month),
              datasets: [
                {
                  label: 'Revenus',
                  data: data.map(d => d.income),
                  borderColor: '#4caf50',
                  backgroundColor: 'rgba(76, 175, 80, 0.1)',
                  tension: 0.4,
                  fill: true
                },
                {
                  label: 'Dépenses',
                  data: data.map(d => d.expenses),
                  borderColor: '#f44336',
                  backgroundColor: 'rgba(244, 67, 54, 0.1)',
                  tension: 0.4,
                  fill: true
                }
              ]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: {
                  position: 'top'
                },
                title: {
                  display: true,
                  text: 'Évolution Revenus vs Dépenses (6 derniers mois)'
                }
              },
              scales: {
                y: {
                  beginAtZero: true,
                  ticks: {
                    callback: (value) => value + ' €'
                  }
                }
              }
            }
          });
        }
      }
    });
  }

  loadCategoryChart(): void {
    this.statisticsService.getCategoryExpenses().subscribe({
      next: (data: CategoryExpense[]) => {
        const ctx = this.categoryChartRef.nativeElement.getContext('2d');
        if (ctx) {
          this.categoryChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
              labels: data.map(d => d.categoryName),
              datasets: [{
                data: data.map(d => d.amount),
                backgroundColor: data.map(d => d.color),
                borderWidth: 2,
                borderColor: '#fff'
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: {
                  position: 'right'
                },
                title: {
                  display: true,
                  text: 'Répartition des dépenses par catégorie (ce mois)'
                },
                tooltip: {
                  callbacks: {
                    label: (context) => {
                      const label = context.label || '';
                      const value = context.parsed || 0;
                      const total = (context.dataset.data as number[]).reduce((a, b) => a + b, 0);
                      const percentage = ((value / total) * 100).toFixed(1);
                      return `${label}: ${value.toFixed(2)} € (${percentage}%)`;
                    }
                  }
                }
              }
            }
          });
        }
      }
    });
  }

  loadBudgetChart(): void {
    this.statisticsService.getBudgetProgress().subscribe({
      next: (data: BudgetProgress[]) => {
        const ctx = this.budgetChartRef.nativeElement.getContext('2d');
        if (ctx) {
          this.budgetChart = new Chart(ctx, {
            type: 'bar',
            data: {
              labels: data.map(d => d.categoryName),
              datasets: [
                {
                  label: 'Dépensé',
                  data: data.map(d => d.spent),
                  backgroundColor: data.map(d => d.color),
                  borderWidth: 0
                },
                {
                  label: 'Restant',
                  data: data.map(d => d.remaining),
                  backgroundColor: 'rgba(200, 200, 200, 0.3)',
                  borderWidth: 0
                }
              ]
            },
            options: {
              indexAxis: 'y',
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: {
                  position: 'top'
                },
                title: {
                  display: true,
                  text: 'Budgets : Dépensé vs Restant'
                },
                tooltip: {
                  callbacks: {
                    label: (context) => {
                      const label = context.dataset.label || '';
                      const value = context.parsed.x || 0;
                      return `${label}: ${value.toFixed(2)} €`;
                    }
                  }
                }
              },
              scales: {
                x: {
                  stacked: true,
                  ticks: {
                    callback: (value) => value + ' €'
                  }
                },
                y: {
                  stacked: true
                }
              }
            }
          });
        }
      }
    });
  }

  loadGoalsChart(): void {
    if (!this.stats) return;
    
    this.statisticsService.getGoalProjections(this.stats.monthlyBalance).subscribe({
      next: (data: GoalProjection[]) => {
        const ctx = this.goalsChartRef.nativeElement.getContext('2d');
        if (ctx) {
          this.goalsChart = new Chart(ctx, {
            type: 'bar',
            data: {
              labels: data.map(d => d.goalName),
              datasets: [
                {
                  label: 'Actuel',
                  data: data.map(d => d.current),
                  backgroundColor: '#2196f3',
                  borderWidth: 0
                },
                {
                  label: 'Projection (+1 mois)',
                  data: data.map(d => d.projected - d.current),
                  backgroundColor: '#64b5f6',
                  borderWidth: 0
                },
                {
                  label: 'Restant',
                  data: data.map(d => Math.max(0, d.target - d.projected)),
                  backgroundColor: 'rgba(200, 200, 200, 0.3)',
                  borderWidth: 0
                }
              ]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: {
                  position: 'top'
                },
                title: {
                  display: true,
                  text: 'Objectifs : Projection d\'épargne avec allocation'
                },
                tooltip: {
                  callbacks: {
                    label: (context) => {
                      const label = context.dataset.label || '';
                      const value = context.parsed.y || 0;
                      return `${label}: ${value.toFixed(2)} €`;
                    },
                    footer: (items) => {
                      const index = items[0].dataIndex;
                      const goal = data[index];
                      return `Allocation: ${goal.allocationPercentage}%`;
                    }
                  }
                }
              },
              scales: {
                x: {
                  stacked: true
                },
                y: {
                  stacked: true,
                  beginAtZero: true,
                  ticks: {
                    callback: (value) => value + ' €'
                  }
                }
              }
            }
          });
        }
      }
    });
  }

  loadTopExpensesChart(): void {
    this.statisticsService.getTopExpenses(5).subscribe({
      next: (data: TopExpense[]) => {
        const ctx = this.topExpensesChartRef.nativeElement.getContext('2d');
        if (ctx) {
          this.topExpensesChart = new Chart(ctx, {
            type: 'bar',
            data: {
              labels: data.map(d => d.description.length > 20 ? d.description.substring(0, 20) + '...' : d.description),
              datasets: [{
                label: 'Montant',
                data: data.map(d => d.amount),
                backgroundColor: data.map(d => d.color),
                borderWidth: 0
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: {
                  display: false
                },
                title: {
                  display: true,
                  text: 'Top 5 des dépenses du mois'
                },
                tooltip: {
                  callbacks: {
                    label: (context) => {
                      const expense = data[context.dataIndex];
                      return [
                        `Montant: ${expense.amount.toFixed(2)} €`,
                        `Catégorie: ${expense.categoryName}`,
                        `Date: ${new Date(expense.date).toLocaleDateString('fr-FR')}`
                      ];
                    }
                  }
                }
              },
              scales: {
                y: {
                  beginAtZero: true,
                  ticks: {
                    callback: (value) => value + ' €'
                  }
                }
              }
            }
          });
        }
      }
    });
  }

  ngOnDestroy(): void {
    if (this.trendsChart) this.trendsChart.destroy();
    if (this.categoryChart) this.categoryChart.destroy();
    if (this.budgetChart) this.budgetChart.destroy();
    if (this.goalsChart) this.goalsChart.destroy();
    if (this.topExpensesChart) this.topExpensesChart.destroy();
  }
}
