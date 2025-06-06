import { Component } from '@angular/core';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';
import { HOME_ROUTES } from './home.routes';
import { HeaderComponent } from '../../components/header/header.component';

@Component({
  selector: 'app-home',
  imports: [SidebarComponent, RouterOutlet, HeaderComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  routes = HOME_ROUTES;
}
