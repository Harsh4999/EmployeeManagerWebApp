import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { User } from 'src/app/model/user';
import { Subscription } from 'rxjs';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { HeaderType } from 'src/app/enum/header-type.enum';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  public showLoading: boolean;
  subscription: Subscription[] = []

  constructor(private router: Router, private authenticationService: AuthenticationService,
    private notifiier: NotificationService) { }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/user/management');
    }
    else {
      this.router.navigateByUrl('/login')
    }
  }

  onLogin(user: User) {
    this.showLoading = true;    

    this.subscription.push(
      this.authenticationService.login(user).subscribe(
        (response: HttpResponse<User>) => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);          
          this.authenticationService.saveToken(token);
          this.authenticationService.addUserToLocalCache(response.body);
          this.router.navigateByUrl('/user/management')
          this.showLoading = false
        },
        (errorResponse: HttpErrorResponse) => {         
          this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message)
          this.showLoading =false
        }))
  }

  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notifiier.notify(notificationType, message);
    }
    else {      
      this.notifiier.notify(notificationType, 'An Error Occured.Please Try Again!');      
    }
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe())
  }
}
