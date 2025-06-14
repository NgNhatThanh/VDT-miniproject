import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from './keycloak.service';
import { catchError, from, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const includeBearerTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);
  const router = inject(Router);
  const token = keycloak.keycloak.token;

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError(error => {
      if (error.status === 401) {
        // Chỉ refresh token khi nhận lỗi 401
        return from(keycloak.keycloak.updateToken(30)).pipe(
          catchError(() => {
            // Nếu không thể refresh token, redirect về trang chủ
            router.navigate(['/']);
            return throwError(() => error);
          }),
          switchMap(() => {
            // Sau khi refresh token thành công, thử lại request với token mới
            const newToken = keycloak.keycloak.token;
            const newReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });
            return next(newReq);
          })
        );
      }
      return throwError(() => error);
    })
  );
};