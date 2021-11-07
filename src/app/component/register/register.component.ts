import { Component, OnInit, OnDestroy } from '@angular/core';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
   showLoading: boolean;
  private subscription: Subscription[] = []
  constructor(private authenticationService: AuthenticationService, private router: Router,
    private notifiier: NotificationService) { }


  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/user/management');
    }
  }

  onRegister(user: User): void {
    console.log('user:', user);
    this.authenticationService.register(user).subscribe(
      (response: User) => {
        this.showLoading = false;
        this.sendErrorNotification(NotificationType.SUCCESS, `A new account was created for ${response.firstName}.
        Please check your email for password to log in.`)
        this.router.navigateByUrl('/user/login');
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message)
      })
  }

  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notifiier.notify(notificationType, message);
    }
    else {
      this.showLoading = false;
      this.notifiier.notify(notificationType, 'An Error Occured.Please Try Again!');
    }
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe())
  }
}
