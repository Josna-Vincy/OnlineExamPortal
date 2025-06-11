// question.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question } from '../../model/interfaces/question';
import { QuestionDTO } from '../../model/interfaces/question-dto'; 
import { Exam } from '../../model/interfaces/exam';

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
  providedIn: 'root',
})
export class QuestionService {
  private apiUrl = 'http://localhost:8090/examiner';
  http = inject(HttpClient);

  constructor() {}

  createQuestion(question: QuestionDTO): Observable<ApiResponse<Question>> {
    return this.http.post<ApiResponse<Question>>(`${this.apiUrl}/createQuestion`, question);
  }

  createMultipleQuestions(questions: QuestionDTO[]): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/createMultipleQuestions`, questions);
  }

  createByCategory(category: string, questions: QuestionDTO[]): Observable<ApiResponse<string>> {
    const params = { category: category };
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/createByCategory`, questions, { params });
  }

  deleteQuestion(id: number): Observable<any> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/delete/${id}`);
  }

  getAllQuestions(): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/allQuestions`);
  }

  updateExam(examId: number, examData: ExamDTO): Observable<ApiResponse<Exam>> {
    return this.http.put<ApiResponse<Exam>>(`${this.apiUrl}/updateExam/${examId}`, examData);
  }

  updateQuestion(questionId: number, questionData: QuestionDTO): Observable<ApiResponse<Question>> {
    return this.http.put<ApiResponse<Question>>(`${this.apiUrl}/updateQuestion/${questionId}`, questionData);
  }

}