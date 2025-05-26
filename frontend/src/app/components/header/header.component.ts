import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { KeycloakService } from '../../services/keycloak/keycloak.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  keycloakService = inject(KeycloakService)
}
