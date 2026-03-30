class SignupPage {

    static init() {
        new PasswordValidator('password', 'passwordConfirm');
    }
}

document.addEventListener('DOMContentLoaded', () => SignupPage.init());
