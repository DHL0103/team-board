// th:field="*{password}" → id="password", th:field="*{passwordConfirm}" → id="passwordConfirm"
const form = document.querySelector('form');
const passwordInput = document.getElementById('password');
const confirmInput = document.getElementById('passwordConfirm');

function getOrCreateMismatchMsg() {
    let msg = confirmInput.parentElement.querySelector('.input-error-msg');
    if (!msg) {
        msg = document.createElement('span');
        msg.className = 'input-error-msg';
        msg.textContent = '비밀번호가 일치하지 않습니다.';
        confirmInput.parentElement.appendChild(msg);
    }
    return msg;
}

function validatePasswords() {
    const mismatch = confirmInput.value.length > 0 && passwordInput.value !== confirmInput.value;
    const msg = getOrCreateMismatchMsg();
    msg.classList.toggle('visible', mismatch);
    confirmInput.classList.toggle('input-error', mismatch);
}

passwordInput.addEventListener('input', validatePasswords);
confirmInput.addEventListener('input', validatePasswords);

form.addEventListener('submit', (e) => {
    if (passwordInput.value !== confirmInput.value) {
        e.preventDefault();
        const msg = getOrCreateMismatchMsg();
        msg.classList.add('visible');
        confirmInput.classList.add('input-error');
        confirmInput.focus();
    }
});
