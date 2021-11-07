import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { NotificationType } from '../enum/notification-type.enum';

@Injectable({
  providedIn: 'root'
})

export class AuthenticationGuard implements CanActivate {

  constructor(private notificationService: NotificationService, private authenticationService: AuthenticationService, private router: Router) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    return this.isUserLoggedIn();
  }

 private isUserLoggedIn(): boolean {       
    if (this.authenticationService.isLoggedIn()) {
      return true;
    }
    
    this.router.navigateByUrl('/login');
    this.notificationService.notify(NotificationType.ERROR, 'You Need to Login to Access This Page!')
    return false;
  }

}
