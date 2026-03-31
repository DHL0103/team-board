class SignupPage {

    static #initialized = false;

    static init() {
        if (SignupPage.#initialized) { return; }
        SignupPage.#initialized = true;

        new PasswordValidator('password', 'passwordConfirm');
    }
}

document.addEventListener('DOMContentLoaded', () => SignupPage.init());
