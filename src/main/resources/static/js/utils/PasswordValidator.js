class PasswordValidator {

    #passwordInput;
    #confirmInput;
    #msgClass;
    #msgText;

    constructor(passwordInputId, confirmInputId, errorClass, errorMessage) {
        this.#passwordInput = document.getElementById(passwordInputId);
        this.#confirmInput  = document.getElementById(confirmInputId);
        if (!this.#passwordInput || !this.#confirmInput) { return; }

        this.#msgClass = errorClass ?? 'input-error-msg';
        this.#msgText  = errorMessage ?? '비밀번호가 일치하지 않습니다.';

        this.#passwordInput.addEventListener('input', () => this.#validate());
        this.#confirmInput.addEventListener('input', () => this.#validate());

        const form = this.#confirmInput.closest('form');
        if (form) {
            form.addEventListener('submit', e => {
                if (this.#passwordInput.value !== this.#confirmInput.value) {
                    e.preventDefault();
                    this.#getOrCreateMsg().classList.add('visible');
                    this.#confirmInput.classList.add('input-error');
                    this.#confirmInput.focus();
                }
            });
        }
    }

    #getOrCreateMsg() {
        let msg = this.#confirmInput.parentElement.querySelector('.' + this.#msgClass);
        if (!msg) {
            msg = document.createElement('span');
            msg.className   = this.#msgClass;
            msg.textContent = this.#msgText;
            this.#confirmInput.parentElement.appendChild(msg);
        }
        return msg;
    }

    #validate() {
        const mismatch = this.#confirmInput.value.length > 0
            && this.#passwordInput.value !== this.#confirmInput.value;
        this.#getOrCreateMsg().classList.toggle('visible', mismatch);
        this.#confirmInput.classList.toggle('input-error', mismatch);
    }
}
