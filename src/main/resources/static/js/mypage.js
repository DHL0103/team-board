const newPasswordInput = document.getElementById('newPassword');
const confirmInput = document.getElementById('newPasswordConfirm');

function getOrCreateMismatchMsg() {
    let msg = confirmInput.parentElement.querySelector('.mypage-input-error-msg');
    if (!msg) {
        msg = document.createElement('span');
        msg.className = 'mypage-input-error-msg';
        msg.textContent = '새 비밀번호가 일치하지 않습니다.';
        confirmInput.parentElement.appendChild(msg);
    }
    return msg;
}

function validatePasswords() {
    const mismatch = confirmInput.value.length > 0 && newPasswordInput.value !== confirmInput.value;
    const msg = getOrCreateMismatchMsg();
    msg.classList.toggle('visible', mismatch);
    confirmInput.classList.toggle('input-error', mismatch);
}

newPasswordInput.addEventListener('input', validatePasswords);
confirmInput.addEventListener('input', validatePasswords);

document.querySelector('form').addEventListener('submit', (e) => {
    if (newPasswordInput.value !== confirmInput.value) {
        e.preventDefault();
        const msg = getOrCreateMismatchMsg();
        msg.classList.add('visible');
        confirmInput.classList.add('input-error');
        confirmInput.focus();
    }
});