// admin-service.service.ts
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { User } from '../../model/interfaces/user';
import { Report } from '../../model/interfaces/report'; // Import Report interface

interface ApiResponse<T> {
  success: boolean;
  status: string;
  data: T;
  errorMessage: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  http = inject(HttpClient);
  private baseUrl = 'http://localhost:8090/admin';

  constructor() { }

  getAllUsers(): Observable<User[]> {
    return this.http.get<ApiResponse<User[]>>(this.baseUrl + '/getAllUsers').pipe(
      map(response => {
        if (response.success && response.data) {
          console.log('Users fetched successfully:', response.data);
          return response.data;
        } else {
          console.error('Error fetching users:', response.errorMessage);
          return []; // Return an empty array in case of error
        }
      })
    );
  }

  deleteUser(username: string): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.baseUrl}/delete/${username}`);
  }

  getAllReports(): Observable<Report[]> {
    return this.http.get<ApiResponse<Report[]>>(this.baseUrl + '/getAllReports').pipe(
      map(response => {
        if (response.success && response.data) {
          console.log('Reports fetched successfully:', response.data);
          return response.data;
        } else {
          console.error('Error fetching reports:', response.errorMessage);
          return []; // Return an empty array in case of error
        }
      })
    );
  }
}