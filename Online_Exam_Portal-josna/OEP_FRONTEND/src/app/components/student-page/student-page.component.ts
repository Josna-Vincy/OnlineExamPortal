import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { StudentService } from '../../services/student/student.service'; // Import StudentService
import { Exam } from '../../model/interfaces/exam'; // Import your Exam interface
import { User } from '../../model/interfaces/user'; // Import your User interface
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Report } from '../../model/interfaces/report';
import { HeaderComponent } from '../header/header.component';

interface ApiResponse<T> {
  success: boolean;
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

@Component({
  selector: 'app-student-page',
  templateUrl: './student-page.component.html',
  styleUrls: ['./student-page.component.css'],
  imports:[CommonModule, FormsModule]
})
export class StudentPageComponent implements OnInit {
  selectedOption: string = 'availableExams'; // Default to show exams

  availableExams: Exam[] = [];

  reports: Report[] = [];

  loggedInUsername: string = ''; // To store the logged-in username
  userProfile: User | null = null;

  leaderboard: LeaderboardEntry[] = []; // Add leaderboard property
  selectedExamId: number | null = null; 

  showPasswordForm: boolean = false;
  newPassword: string = '';
  confirmPassword: string = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private studentService: StudentService // Inject StudentService
  ) { }

  ngOnInit(): void {
    this.loadLoggedInUsername();
    this.loadAvailableExams();
  }

  selectOption(option: string): void {
    this.selectedOption = option;
    console.log(`Selected option: ${this.selectedOption}`);
    if (option === 'availableExams') {
      this.loadAvailableExams();
    } else if (option === 'attendExam') {
      console.log(this.loggedInUsername);
      console.log('Attend Exam section clicked.');
    } else if (option === 'viewReport') {
      console.log('Get Report section clicked.');
      this.loadReports();
    } else if (option === 'leaderboard') {
      console.log('View Leaderboard section clicked.');
      this.loadLeaderboard();
      this.showPasswordForm = false; // Hide password form if visible
    } else if( option === 'profile') {
      console.log('Profile section clicked.');
      this.loadUserProfile(); 
    }
  }

  loadReports(): void {
    if (this.loggedInUsername) {
      this.studentService.getReports(this.loggedInUsername).subscribe(
        (data) => {
          if (Array.isArray(data)) {
            console.log('Reports loaded:', data);
            this.reports = data;
          } else if (data) {
            // It's a single report object
            this.reports = [data]; // Wrap it in an array so *ngFor works
          } else {
            this.reports = []; // No reports
          }
        },
        (error) => {
          console.error('Error fetching reports:', error);
          this.reports = []; // Handle errors by clearing the reports array
        }
      );
    } else {
      console.error('Username not found in local storage.');
      this.reports = []; // Handle missing username
    }
  }

  logout(): void {
    this.authService.removeToken();
    this.router.navigate(['/login']);
  }

  loadLoggedInUsername(): void {
    const token = this.authService.getToken();
    if (token) {
      const decodedToken: any = JSON.parse(atob(token.split('.')[1]));
      this.loggedInUsername = decodedToken?.sub || '';
    }
    console.log('Logged-in username:', this.loggedInUsername);
  }

  loadUserProfile(): void {
    if (this.loggedInUsername) {
      this.studentService.getProfile(this.loggedInUsername).subscribe({
        next: (response: ApiResponse<User>) => {
          if (response.success && response.data) {
            this.userProfile = response.data;
            console.log('User profile loaded:', this.userProfile);
          } else {
            console.error('Error loading user profile:', response.errorMessage);
            // Handle error appropriately
          }
        },
        error: (error) => {
          console.error('Error loading user profile:', error);
          // Handle error appropriately
        }
      });
    } else {
      console.warn('Username not found, cannot load profile.');
    }
  }

  loadAvailableExams(): void {
    this.studentService.getExams().subscribe(
      (response: ApiResponse<Exam[]>) => {
        if (response.success && response.data) {
          this.availableExams = response.data;
          console.log('Available exams loaded:', this.availableExams);
        } else {
          console.error('Error loading available exams:', response.errorMessage);
        }
      },
      (error) => {
        console.error('Error loading available exams:', error);
      }
    );
  }

  loadLeaderboard(): void {
    if (this.selectedExamId) {
      this.studentService.getLeaderboard(this.selectedExamId).subscribe(
        (data) => {
          this.leaderboard = data;
        },
        (error) => {
          console.error('Error loading leaderboard:', error);
          this.leaderboard = [];
        }
      );
    }
  }

  goToAttendExam(examId: number, username: string): void {
    this.router.navigate(['/student/attend-exam', examId], {
      queryParams: { username: username } // Pass username as query parameter
    });
  }

  showUpdatePasswordForm(): void {
    this.showPasswordForm = true;
  }

  oldPassword: string = '';
  closePasswordForm(): void {
    this.showPasswordForm = false;
    this.oldPassword = ''; // Reset oldPassword
    this.newPassword = '';
    this.confirmPassword = '';
  }

  updatePassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      alert('Passwords do not match.');
      return;
    }

    if (!this.loggedInUsername) {
      alert('User not logged in.');
      return;
    }

    // Cast the role to the correct literal type
    const role = this.userProfile?.roles[0] || 'STUDENT';
    const updateRequest = {
      userName: this.loggedInUsername,
      email: this.userProfile?.email || '',
      role: role as 'STUDENT' | 'EXAMINER' | 'ADMIN',
      password: this.oldPassword, // Include oldPassword
    };

    this.authService.updatePassword(updateRequest, this.newPassword).subscribe(
      (response) => {
        alert('Password updated successfully.');
        this.closePasswordForm();
      },
      (error) => {
        console.error('Error updating password:', error);
        alert('Error updating password. Please try again.');
      }
    );
  }
}