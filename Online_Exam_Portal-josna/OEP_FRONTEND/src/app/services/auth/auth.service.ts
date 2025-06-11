import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LoginRequest } from '../../model/interfaces/login-request';
import { Observable, tap } from 'rxjs';
import { AuthResponse } from '../../model/interfaces/auth-response';
import { jwtDecode, JwtPayload } from 'jwt-decode';

interface JwtPayloadWithRole extends JwtPayload {
  userId?: string; // Assuming your JWT contains userId
  sub?: string; // Assuming your JWT contains username ('sub' is common)
}

interface UserRequest {
  userName: string;
  email: string;
  role: 'EXAMINER' | 'ADMIN' | 'STUDENT';
  password: string;
}

interface RegistrationResponse {
  success: boolean;
  status: string;
  data: { // Adjust the 'data' structure based on your needs
    userId: string;
    userName: string;
    email: string;
    roles: string[];
  };
  errorMessage: string | null;
}
interface UserRoleResponse {
  data: string; // The role will be in the 'data' field of your Response
  statusCode: number;
  message: string | null;
  success: boolean;
}

interface Response<T> {
  success: boolean;
  data?: T;
  errorMessage?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  http = inject(HttpClient);
  private tokenKey = 'authToken';
  private baseUrl = 'http://localhost:8090/api/auth'; // Base URL for authentication

  constructor() { }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap((response) => {
        if (response && response.data) {
          this.saveToken(response.data);
        }
      })
    );
  }

  registerUser(userRequest: UserRequest): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(this.baseUrl + '/register', userRequest);
  }


  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getUsernameFromToken(): string | null {
    const token = this.getToken();
    console.log('Token:', token); // Debugging line
    if (token) {
      try {
        const decodedToken: JwtPayloadWithRole = jwtDecode(token);
        return decodedToken.sub || null; // Assuming 'sub' contains the username
      } catch (error) {
        console.error('Error decoding JWT:', error);
        return null;
      }
    }
    return null;
  }

  fetchUserRole(): Observable<UserRoleResponse> {
    const username = this.getUsernameFromToken();
    const token = this.getToken();

    if (!username) {
      throw new Error('Username not found in token.');
    }
    if (!token) {
      throw new Error('Authentication token not found.');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}` // Assuming your backend requires Bearer token
    });

    return this.http.get<UserRoleResponse>(`${this.baseUrl}/getRole?username=${username}`, { headers });
  }

  updatePassword(request: UserRequest , newPassword: string): Observable<Response<any>> {
    return this.http.put<Response<any>>(`${this.baseUrl}/updatePassword?newPassword=${newPassword}`, request);
  }
}