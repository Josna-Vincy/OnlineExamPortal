import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Exam } from '../../model/interfaces/exam';
import { User } from '../../model/interfaces/user';
import { Report } from '../../model/interfaces/report';
import { ExamResponseDTO } from '../../model/interfaces/exam-response-dto';

interface ApiResponse<T> {
  success: boolean;
  string?: string;
  data?: T;
  errorMessage?: string;
}

interface LeaderboardEntry {
  id: number;
  examId: number;
  username: string;
  marks: number;
  position: number;
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  http = inject(HttpClient);

  private baseUrl = 'http://localhost:8090/student';

  constructor() { }

  getExams(): Observable<ApiResponse<Exam[]>> {
    return this.http.get<ApiResponse<Exam[]>>(`${this.baseUrl}/getExams`);
  }

  getProfile(username: string): Observable<ApiResponse<User>> {
    const params = new HttpParams().set('username', username);
    return this.http.get<ApiResponse<User>>(`${this.baseUrl}/profile`, { params });
  }

  getReports(username: string): Observable<Report[]> {
    return this.http.get<ApiResponse<Report[]>>(`${this.baseUrl}/report/${username}`).pipe(
      map(response => {
        if (response && response.data) {
          return response.data;
        } else {
          return []; // Return an empty array if data is missing
        }
      })
    );
  }

  getLeaderboard(examId: number): Observable<LeaderboardEntry[]> {
    return this.http.get<ApiResponse<LeaderboardEntry[]>>(`${this.baseUrl}/leaderboard/${examId}`).pipe(
      map(response => {
        if (response && response.data) {
          return response.data;
        } else {
          return [];
        }
      })
    );
  }

  submitResponse(responseDTO: ExamResponseDTO): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/submitResponse`, responseDTO);
  }
}