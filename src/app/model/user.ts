export class User {
    id: number
    userId: string;
    firstName: string;
    lastName: string;
    username: string;
    password: string;
    email: string;
    profileImgUrl: string;
    lastLoginDate: Date;
    lastLoginDateDisplay: Date
    joinDate: Date
    role: string;
    authorities: [];
    active: boolean
    notLocked: boolean

    constructor() {
        this.id = 0;
        this.userId = '';
        this.firstName = '';
        this.lastName = '';
        this.username = '';
        this.email = '';
        this.profileImgUrl = null
        this.lastLoginDate = null
        this.lastLoginDateDisplay = null
        this.joinDate = null
        this.active = false;
        this.notLocked = false;
        this.role = '';
        this.authorities = [];
    }
}
