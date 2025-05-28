import { Component } from '@angular/core';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';
import { HOME_ROUTES } from './home.routes';

@Component({
  selector: 'app-home',
  imports: [SidebarComponent, RouterOutlet],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  routes = HOME_ROUTES;
}
