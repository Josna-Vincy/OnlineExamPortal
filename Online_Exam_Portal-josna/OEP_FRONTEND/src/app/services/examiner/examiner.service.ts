import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Exam } from '../../model/interfaces/exam'; // Adjust the path
import { Report } from '../../model/interfaces/report';


interface ApiResponse<T> {
  success: boolean;
  status: string;
  data: T | null;
  errorMessage: string | null;
}

interface ExamDTO {
  title: string;
  description?: string;
  totalMarks: number | null;
  duration: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class ExaminerService {

  http = inject(HttpClient);
  private baseUrl = 'http://localhost:8090/examiner';

  constructor() { }

  getAllExams(): Observable<Exam[]> {
    return this.http.get<Exam[]>(this.baseUrl + '/allExams').pipe(
      map(exams => {
        console.log('Exams fetched successfully:', exams);
        return exams;
      })
    );
  }

  createExam(category: string, examDTO: ExamDTO): Observable<ApiResponse<Exam>> {
    return this.http.post<ApiResponse<Exam>>(`${this.baseUrl}/createExam/${category}`, examDTO);
  }

  deleteExam(examId: number): Observable<ApiResponse<Exam>> {
    return this.http.delete<ApiResponse<Exam>>(`${this.baseUrl}/deleteExam/${examId}`);
  }

  evaluateExam(examId: number, username: string): Observable<ApiResponse<Report>> {
    return this.http.get<ApiResponse<Report>>(`${this.baseUrl}/evaluate/${examId}/${username}`);
  }

  getExamById(examId: number): Observable<ApiResponse<Exam>> {
    return this.http.get<ApiResponse<Exam>>(`${this.baseUrl}/getExam/${examId}`);
  }

  // In examiner.service.ts
  updateFeedback(username: string, examId: number, feedback: string): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/updateFeedback/${examId}/${username}`, feedback, { headers: { 'Content-Type': 'text/plain' } });
  }
}