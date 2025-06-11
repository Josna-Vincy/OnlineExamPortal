import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { AdminService } from '../../services/admin/admin-service.service';
import { ExaminerService } from '../../services/examiner/examiner.service';
import { StudentService } from '../../services/student/student.service';
import { User } from '../../model/interfaces/user';
import { Exam } from '../../model/interfaces/exam';
import { FormsModule } from '@angular/forms';
import { Report } from '../../model/interfaces/report';

interface ApiResponse<T> {
  success: boolean;
  string?: string;
  data?: T | null;
  errorMessage: string | null;
}

//interfacing UserRequest
interface UserRequest {
  userName: string;
  email: string;
  role: 'EXAMINER' | 'ADMIN' | 'STUDENT';
  password: string;
}

//interfacing LeaderboardEntry
interface LeaderboardEntry {
  id: number;
  examId: number;
  username: string;
  marks: number;
  position: number;
}

@Component({
  selector: 'app-admin-page',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-page.component.html',
  styleUrl: './admin-page.component.css'
})
export class AdminPageComponent implements OnInit {
  // General selection variable
  selectedOption: string | null = null;

  // Registration related variables
  registrationData: UserRequest = {
    userName: '',
    email: '',
    role: 'STUDENT',
    password: '',
  };
  registrationSuccessMessage: string = '';
  registrationErrorMessage: string = '';
  showSuccessPopup: boolean = false;

  // User data variables
  users: User[] = [];
  loggedInUsername: string = ''; 
  userProfile: User | null = null;

  // Password update variables
  showPasswordForm: boolean = false;
  oldPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  // Exam data variables
  exams: Exam[] = [];
  selectedExamId: number | null = null;
  leaderboard: LeaderboardEntry[] = [];

  // Report data variables
  reports: Report[] = [];
  selectedReport: Report | null = null; // To hold the selected report for the popup
  showReportPopup: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private adminService: AdminService,
    private examinerService: ExaminerService,
    private studentService: StudentService
  ) { }

  ngOnInit(): void {
    // Load initial data or set default view if needed
    this.selectOption(null); // Or a default option like 'users'
    this.loadExams();
    this.loadLoggedInUsername();
    this.loadUserProfile();
  }

  // Navigation and selection
  selectOption(option: string | null): void {
    this.selectedOption = option;
    console.log('Selected option:', option);

    if (this.selectedOption === 'users') {
      this.loadUsers();
      this.showPasswordForm = false;
      this.closeReportPopup();
    } else if (this.selectedOption === 'exams') {
      this.loadExams();
      this.showPasswordForm = false;
      this.closeReportPopup();
    } else if (this.selectedOption === 'reports') {
      this.loadReports();
      this.showPasswordForm = false;
      this.closeReportPopup();
    } else if (this.selectedOption === 'profileUpdate') {
      console.log('Profile update selected');
      this.closeReportPopup();
    } else if (this.selectedOption === 'leaderboard') {
      this.loadLeaderboard();
      this.showPasswordForm = false;
      this.closeReportPopup();
    } else if (this.selectedOption === 'profile') {
      this.loadLoggedInUsername();
      this.loadUserProfile();
      this.closeReportPopup();
    } else {
      this.closeReportPopup();
    }
  }

  // Registration functionality
  registerUser(): void {
    this.registrationSuccessMessage = '';
    this.registrationErrorMessage = '';

    this.authService.registerUser(this.registrationData).subscribe(
      (response) => {
        console.log('Registration successful:', response);
        this.showSuccessPopup = true;
        setTimeout(() => {
          this.registrationData = { userName: '', email: '', role: 'STUDENT', password: '' };
          this.showSuccessPopup = false;
        }, 2000);
      },
      (error) => {
        console.error('Registration failed:', error);
        this.registrationErrorMessage = error?.error?.message || 'Registration failed. Please try again.';
      }
    );
  }

  // Profile functionality
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
        next: (response) => {
          const typedResponse = response as ApiResponse<User>; // Explicit cast
          if (typedResponse.success && typedResponse.data) {
            this.userProfile = typedResponse.data;
            console.log('User profile loaded:', this.userProfile);
          } else {
            console.error('Error loading user profile:', typedResponse.errorMessage);
          }
        },
        error: (error) => {
          console.error('Error loading user profile:', error);
        }
      });
    } else {
      console.warn('Username not found, cannot load profile.');
    }
  }

  showUpdatePasswordForm(): void {
    this.showPasswordForm = true;
  }

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
    const role = this.userProfile?.roles[0] || 'EXAMINER';
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

  // Exam management functionality
  loadExams(): void {
    this.examinerService.getAllExams().subscribe(
      (data) => {
        this.exams = data;
        console.log('Exams loaded:', this.exams);
      },
      (error) => {
        console.error('Error loading exams:', error);
      }
    );
  }

  loadLeaderboard(): void {
    if (this.selectedExamId) {
      this.studentService.getLeaderboard(this.selectedExamId).subscribe( // Use StudentService
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

  // Report management functionality
  loadReports(): void {
    this.adminService.getAllReports().subscribe(
      (data) => {
        this.reports = data;
        console.log('Reports loaded:', this.reports);
      },
      (error) => {
        console.error('Error loading reports:', error);
      }
    );
  }

  viewReportDetails(report: Report): void {
    this.selectedReport = report;
    this.showReportPopup = true;
  }

  closeReportPopup(): void {
    this.showReportPopup = false;
    this.selectedReport = null;
  }

  // User management functionality (moved after profile)
  loadUsers(): void {
    this.adminService.getAllUsers().subscribe(
      (data) => {
        this.users = data;
        console.log('Users loaded:', this.users);
      },
      (error) => {
        console.error('Error loading users:', error);
      }
    );
  }

  deleteUser(username: string): void {
    if (confirm(`Are you sure you want to delete user "${username}"?`)) {
      this.adminService.deleteUser(username).subscribe(
        (response) => {
          console.log('User deleted successfully:', response);
          this.loadUsers();
          alert(`User "${username}" deleted successfully.`);
        },
        (error) => {
          console.error('Error deleting user:', error);
          alert(`Error deleting user "${username}". Please try again.`);
        }
      );
    }
  }

  // Logout functionality
  logout(): void {
    this.authService.removeToken();
    this.router.navigate(['/login']);
  }
}