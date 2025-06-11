import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoginRequest } from '../../model/interfaces/login-request';
import { AuthService } from '../../services/auth/auth.service';
import { HeaderComponent } from '../header/header.component';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, HeaderComponent, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  isLoading: boolean = true;
  credentials: LoginRequest = { username: '', password: '' };
  role: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) { 
  }

  ngOnInit(): void {
    setTimeout(() => {
      this.isLoading = false;
    }, 1000);
  }

  login(): void {
    
      if (!this.credentials.username || !this.credentials.password || !this.role) {
        alert('All fields are required: Username, Password, and Role.');
        return;
      }
  
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).pipe(
      switchMap((response) => {
        console.log('Login successful', response);
        this.isLoading = false;
        return this.authService.fetchUserRole(); // Fetch role after login
      })
    ).subscribe({
      next: (roleResponse) => {
        const role = roleResponse.data; // The role is in the 'data' field
        console.log('User role:', role);
        if (role === '[ADMIN]') {
          this.router.navigate(['/admin']);
        } else if (role === '[EXAMINER]') {
          this.router.navigate(['/examiner']);
        } else if (role === '[STUDENT]') {
          this.router.navigate(['/student']);
        } else {
          console.warn('Unknown role:', role);
          this.router.navigate(['/login']);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Login failed or fetching role failed', error);
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Login failed. Please check your credentials.';
        }
      }
    });
  }
}