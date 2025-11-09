import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Goal, GoalRequest, GoalStatus } from '@core/models/goal.model';

@Injectable({
  providedIn: 'root'
})
export class GoalService {
  private apiUrl = `${environment.apiUrl}/goals`;

  constructor(private http: HttpClient) {}

  getAllGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>(this.apiUrl);
  }

  getActiveGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>(`${this.apiUrl}/active`);
  }

  getGoalsByStatus(status: GoalStatus): Observable<Goal[]> {
    return this.http.get<Goal[]>(`${this.apiUrl}/status/${status}`);
  }

  getGoalById(id: number): Observable<Goal> {
    return this.http.get<Goal>(`${this.apiUrl}/${id}`);
  }

  createGoal(request: GoalRequest): Observable<Goal> {
    return this.http.post<Goal>(this.apiUrl, request);
  }

  updateGoal(id: number, request: GoalRequest): Observable<Goal> {
    return this.http.put<Goal>(`${this.apiUrl}/${id}`, request);
  }

  deleteGoal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  addContribution(id: number, amount: number): Observable<Goal> {
    return this.http.post<Goal>(`${this.apiUrl}/${id}/contribute`, { amount });
  }
}
