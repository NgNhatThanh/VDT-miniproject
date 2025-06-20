import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from './user-profile';
import { environment } from '../../../environments/environment';
import { jwtDecode } from 'jwt-decode';

interface TokenPayload {
  realm_access: {
    roles: string[];
  };
}

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;

  private _profile: UserProfile | undefined;

  get keycloak(){
    if(!this._keycloak){
      this._keycloak = new Keycloak({
        url: environment.keycloakUrl,
        realm: 'VDT',
        clientId: 'quanlycuochop'
      })
    }
    return this._keycloak;
  }

  get profile(){
    return this._profile
  }

  constructor() { }

  async init() {
    console.log("Initializing Keycloak ...")
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required',
      pkceMethod: 'S256',
      checkLoginIframe: false
    })

    if(authenticated){
      const userInfo = await this.keycloak.loadUserInfo() as { preferred_username: string, sub: string }
      this._profile = userInfo as UserProfile
      this._profile.token = this.keycloak.token;
      this._profile.username = userInfo.preferred_username
      this._profile.id = userInfo.sub
      console.log(this._profile.token)
    }

  }
  
  login(){
    return this.keycloak.login();
  }

  logout(){
    return this.keycloak.logout();
  }

  getUserRoles(): string[] {
    if (!this.keycloak.token) {
      return [];
    }
    
    try {
      const tokenPayload = jwtDecode<TokenPayload>(this.keycloak.token);
      return tokenPayload.realm_access?.roles || [];
    } catch (error) {
      console.error('Error parsing token:', error);
      return [];
    }
  }

}
