import { Component, inject, OnInit } from '@angular/core';
import { KeycloakService } from '../../services/keycloak/keycloak.service';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatTreeModule } from '@angular/material/tree';
import { RouterModule, Router } from '@angular/router';
import { environment } from '../../../environments/environment';

export interface SidebarMenuItem {
  label: string;
  icon?: string;
  color?: string;
  active?: boolean;
  url?: string;
  onClick?: () => void;
  children?: SidebarMenuItem[];
}

@Component({
  selector: 'app-sidebar',
  imports: [
    RouterModule,
    CommonModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatExpansionModule,
    MatDividerModule,
    MatTreeModule
  ],
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent implements OnInit {
  kcService = inject(KeycloakService)
  router = inject(Router)

  sidebarMenus: SidebarMenuItem[] = [];

  ngOnInit() {
    this.updateSidebarMenus();
  }

  private updateSidebarMenus() {
    const userRoles = this.kcService.getUserRoles();
    const isAdmin = userRoles.includes('ADMIN');

    this.sidebarMenus = [
      {
        label: 'QUẢN LÝ LỊCH HỌP',
        children: [
          { label: 'Lịch họp cá nhân', icon: 'person', url: '/lich-hop-ca-nhan' },
          ...(isAdmin ? [{ label: 'Tạo lịch họp', icon: 'add', url: '/them-cuoc-hop' }] : [])
        ]
      },
      {
        label: 'TÀI KHOẢN CỦA TÔI',
        children: [
          { label: 'Đăng xuất', icon: 'logout', onClick: () => this.kcService.logout() },
          { label: 'Hồ sơ cá nhân', icon: 'person', onClick: () => window.open(`${environment.keycloakUrl}/realms/VDT/account`) }
        ]
      }
    ];
  }
}
