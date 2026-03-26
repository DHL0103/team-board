var PAGE_SIZE = 10;

var assignedList      = document.getElementById('assigned-list');
var assignedPagination = document.getElementById('assigned-pagination');
if (assignedList && assignedPagination) {
    var assignedItems = Array.from(assignedList.querySelectorAll('.assigned-card'));
    App.makePaginator(assignedItems, assignedPagination, PAGE_SIZE);
}

var inviteList      = document.getElementById('invite-list');
var invitePagination = document.getElementById('invite-pagination');
if (inviteList && invitePagination) {
    var inviteItems = Array.from(inviteList.children);
    App.makePaginator(inviteItems, invitePagination, PAGE_SIZE);
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
App.initPasswordValidation('newPassword', 'newPasswordConfirm', 'mypage-input-error-msg', '새 비밀번호가 일치하지 않습니다.');