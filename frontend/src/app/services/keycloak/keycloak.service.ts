import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from './user-profile';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;

  private _profile: UserProfile | undefined;

  get keycloak(){
    if(!this._keycloak){
      this._keycloak = new Keycloak({
        url: 'http://localhost:9095',
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
      checkLoginIframe: false,
    })

    if(authenticated){
      const userInfo = await this.keycloak.loadUserInfo() as { preferred_username: string }
      this._profile = userInfo as UserProfile
      this._profile.token = this.keycloak.token;
      this._profile.username = userInfo.preferred_username
    }

  }

  login(){
    return this.keycloak.login();
  }

  logout(){
    return this.keycloak.logout();
  }

}
