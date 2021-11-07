import { Component, OnInit, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { User } from 'src/app/model/user';
import { UserService } from 'src/app/service/user.service';
import { NotificationService } from 'src/app/service/notification.service';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { Router } from '@angular/router';
import { FileUploadStatus } from 'src/app/model/file-upload.status';
import { Role } from 'src/app/enum/role.enum';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy {
  private subscription: Subscription[] = []
  private titleSubject = new BehaviorSubject<string>('Users');
  public titleAction$ = this.titleSubject.asObservable();

  private currentusername :string;
  public users: User[];
  public user :User;

  public refreshing: boolean

  //public isAdmin: boolean = true
  //public isAdminOrManager : boolean = true
  //public isManager : boolean = false

  public selectedUser: User
  public fileName: string
  public profileImage: File;

  public editUser = new User();
  public fileStatus = new FileUploadStatus();
  public fileStutus :any;


  constructor(private authenticationService : AuthenticationService,
              private userService: UserService, private router : Router,
              private notifiier: NotificationService) { }
  ngOnInit(): void {
    this.getUsers(true);
    this.user = this.authenticationService.getUserToLocalCache();
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe())
  }

  getUsers(showNotification: boolean) {
    this.refreshing = true;
    this.subscription.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUserToLocalCache(response);
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) found`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.refreshing = false;
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }))
  }

  onSelectUser(selectedUser: User) {
    this.selectedUser = selectedUser;
    this.clickButton('openUserInfo');
  }

  onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
  }

  saveNewUser(): void {
    document.getElementById('new-user-save').click()
  }

  onAddNewUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormData(null, userForm.value, this.profileImage)
    this.subscription.push(this.userService.addUser(formData).subscribe(
      (response: User) => {
        this.clickButton('new-user-close');
        this.getUsers(false);
        this.fileName = null;
        this.profileImage = null;
        userForm.reset();
        this.sendNotification(NotificationType.SUCCESS,
              `${response.firstName} ${response.lastName} Added Successfully!`)
      },
      (responseError: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, responseError.error.message)
        this.profileImage = null;
      }
    ))
  }

  onUpdateUser() : void
 {
  const formData = this.userService.createUserFormData(this.currentusername, this.editUser, this.profileImage)
  this.subscription.push(this.userService.updateUser(formData).subscribe(
    (response: User) => {
      this.clickButton('closeEditUserModalButton');
      this.getUsers(false);
      this.fileName = null;
      this.profileImage = null;
      this.sendNotification(NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} Updated Successfully!`)
    },
    (responseError: HttpErrorResponse) => {
      this.sendNotification(NotificationType.ERROR, responseError.error.message)
      this.profileImage = null;
    }
  ))
 }

  onUpdateCurrentUser(user: User): void {
    this.refreshing = true;
    this.currentusername = this.authenticationService.getUserToLocalCache().username;
    const formData = this.userService.createUserFormData(this.currentusername, this.user, this.profileImage)
    this.subscription.push(this.userService.updateUser(formData).subscribe(
      (response: User) => {
        this.authenticationService.addUserToLocalCache(response)
        this.getUsers(false);
        this.fileName = null;
        this.profileImage = null;
        this.refreshing = false;
        this.sendNotification(NotificationType.SUCCESS,
          `${response.firstName} ${response.lastName} Updated Successfully!`)
      },
      (responseError: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, responseError.error.message)
        this.profileImage = null;
        this.refreshing = false
      }
    ))
  }

  onEditUser(editUser: User) : void {
    this.editUser = editUser;
    this.currentusername = editUser.username;
    this.clickButton('openUserEdit')
 }

  onDeleteUser(username: string) :void {
    this.subscription.push(this.userService.deleteUser(username).subscribe(
      (response: CustomHttpResponse) => {
        console.log('response:',response);

        this.sendNotification(NotificationType.SUCCESS, response.message)
        this.getUsers(false);
      },
      (httpError: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, httpError.error.message)
      }
    ))
  }

  searchUsers(searchTerm: string) : void {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
      ) {
        results.push(user)
      }
    }
    this.users = results;
    if (this.users.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalCache();
    }
  }

  onResetPassword(emailForm: NgForm): void {
    this.refreshing = true;
    const emailAddress: string = emailForm.value['reset-password-email']
    this.subscription.push(
      this.userService.resetPassword(emailAddress).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (httpError: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, httpError.message);
          this.refreshing = false;
        },
        () => emailForm.reset()
      )
    )
  }

  onUpdateProfileImage(): void {
    const formData = new FormData();
    formData.append('username', this.user.username);
    formData.append('profileImage', this.profileImage);

    this.subscription.push(
      this.userService.updateProfileImage(formData).subscribe(
        (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message)
          this.fileStatus.status='done';
        }
      )
    );
  }

  private reportUploadProgress(event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.fileStatus.percentage = Math.round(100 * event.loaded / event.total);
        this.fileStatus.status = 'progress';
        break;
      case HttpEventType.Response:
        if (event.status === 200) {
          this.user.profileImgUrl = `${event.body.profileImageUrl}?time=${new Date().getTime()}`;
          this.sendNotification(NotificationType.SUCCESS, `${event.body.firstName}\s profile image updated Successfully! `);
          this.fileStatus.status = 'done';
          break;
        }
        else {
          this.sendNotification(NotificationType.ERROR, `Unable to load an Image. Please try Again!`);
          break;
        }
      default:
        `Finished all Progress!`;
    }
  }

  updateProfileImage() :void
  {
    this.clickButton('profile-image-input')
  }


  onLogOut() : void
  {
    this.authenticationService.logOut();
    this.router.navigateByUrl('/login')
    this.sendNotification(NotificationType.INFO,`You've been logged out successfully!`)
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.SUPER_ADMIN || this.getUserRole() === Role.ADMIN;
  }

  public get isManager(): boolean {
    return this.isAdmin || this.getUserRole() === Role.MANAGER;
  }

  public get isAdminOrManager(): boolean {
    return this.isAdmin || this.isManager;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserToLocalCache().role;
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notifiier.notify(notificationType, message);
    }
    else {
      this.notifiier.notify(notificationType, 'An Error Occured.Please Try Again!');
    }
  }

  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }

  changeTitle(title: string) {
    this.titleSubject.next(title);
  }

}
