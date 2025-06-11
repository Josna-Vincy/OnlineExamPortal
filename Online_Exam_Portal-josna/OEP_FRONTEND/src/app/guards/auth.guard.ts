import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { Observable, map, tap } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    if (!this.authService.isAuthenticated()) {
      alert('Please log in to access this page.');
      return this.router.parseUrl('/login');
    }

    const requiredRole = route.data['roles'] as string[];

    return this.authService.fetchUserRole().pipe(
      tap(response => console.log('AuthGuard - User Role Response:', response)), // Log the entire response
      map(response => {
        const userRole = response.data; // Access the 'data' property
        const userRoleString = userRole ? userRole.toString() : null;
        console.log('AuthGuard - Required Role:', requiredRole);
        console.log('AuthGuard - User Role (extracted):', userRoleString);

        if (requiredRole && userRoleString) {
          if (requiredRole.includes(userRoleString)) {
            console.log('AuthGuard - Authorized');
            return true;
          } else {
            alert('You are not authorized to access this route.');
            return this.router.parseUrl('/');
          }
        }
        return true; 
      })
    );
  }
}