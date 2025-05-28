import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { KeycloakService } from './services/keycloak/keycloak.service';

export function kcFactory(){
  const kcService = inject(KeycloakService)
  return kcService.init();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideAppInitializer(kcFactory),
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes)
  ],
};
