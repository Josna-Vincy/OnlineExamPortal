import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { AdminPageComponent } from './components/admin-page/admin-page.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { ExaminerPageComponent } from './components/examiner-page/examiner-page.component';
import { StudentPageComponent } from './components/student-page/student-page.component';
import { AuthGuard } from './guards/auth.guard';
import { AttendExamComponent } from './components/attend-exam/attend-exam.component';

export const routes: Routes = [
    { path: '', component:LandingPageComponent },
    { path: 'login', component: LoginComponent },
    { path: 'admin', component: AdminPageComponent ,canActivate: [AuthGuard], data: { roles: ['[ADMIN]'] }},
    { path: 'examiner', component: ExaminerPageComponent, canActivate: [AuthGuard], data: { roles: ['[EXAMINER]'] }},
    { path: 'student', component: StudentPageComponent, canActivate: [AuthGuard], data: { roles: ['[STUDENT]'] }},
    { path: 'student/attend-exam/:examId', component: AttendExamComponent, canActivate: [AuthGuard], data: { roles: ['[STUDENT]'] }}
];


// How did you handle authentication in your project? Explain the flow.
// How did you implement role-based authorization in your project?
// How did you manage data fetching and display in your components?
// Can you walk us through a specific component you developed and explain its functionality and implementation?
// How did you handle form submissions and validation in your project?
// How did you use services to share data or logic across components?
// Did you use any third-party Angular libraries? If so, for what purpose?
// How did you style your Angular application? Did you use Bootstrap or custom CSS?
// How did you handle routing and navigation in your application?