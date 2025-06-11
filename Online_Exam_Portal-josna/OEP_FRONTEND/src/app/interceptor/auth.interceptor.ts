import { HttpInterceptorFn } from '@angular/common/http';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authToken = localStorage.getItem('authToken'); // Replace 'authToken' with your key

  if (authToken) {
    const authRequest = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${authToken}`)
    });
    return next(authRequest); // Use the HttpHandlerFn directly
  }

  return next(req); // Use the HttpHandlerFn directly
};