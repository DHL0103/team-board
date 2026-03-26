var PAGE_SIZE = 10;

var assignedList      = document.getElementById('assigned-list');
var assignedPagination = document.getElementById('assigned-pagination');
if (assignedList && assignedPagination) {
    var assignedItems = Array.from(assignedList.querySelectorAll('.assigned-card'));
    makePaginator(assignedItems, assignedPagination, PAGE_SIZE);
}

var inviteList      = document.getElementById('invite-list');
var invitePagination = document.getElementById('invite-pagination');
if (inviteList && invitePagination) {
    var inviteItems = Array.from(inviteList.children);
    makePaginator(inviteItems, invitePagination, PAGE_SIZE);
}

// ── 사이드바 탭 전환 ──
const sidebarItems = document.querySelectorAll('.sidebar-item');
const sections = document.querySelectorAll('.mypage-section');

sidebarItems.forEach(item => {
    item.addEventListener('click', () => {
        const target = item.dataset.section;

        sidebarItems.forEach(i => i.classList.remove('active'));
        sections.forEach(s => s.classList.remove('active'));

        item.classList.add('active');
        document.getElementById('section-' + target).classList.add('active');
    });
});

// ── 비밀번호 변경 폼 검증 ──
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