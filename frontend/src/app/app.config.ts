import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { KeycloakService } from './services/keycloak/keycloak.service';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { includeBearerTokenInterceptor } from './services/keycloak/keycloak.interceptor';

export function kcFactory(){
  const kcService = inject(KeycloakService)
  return kcService.init()
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideAppInitializer(kcFactory),
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes)
  ],
};
