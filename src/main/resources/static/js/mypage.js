// ── 페이지네이션 헬퍼 ──
function makePaginator(items, paginationEl, pageSize) {
    var currentPage = 1;

    function render() {
        var start = (currentPage - 1) * pageSize;
        var end   = start + pageSize;
        items.forEach(function (item, i) {
            item.style.display = (i >= start && i < end) ? '' : 'none';
        });
        renderPagination();
    }

    function renderPagination() {
        var totalPages = Math.max(1, Math.ceil(items.length / pageSize));
        paginationEl.innerHTML = '';
        if (totalPages <= 1) { return; }

        var prev = document.createElement('button');
        prev.className = 'page-btn';
        prev.textContent = '이전';
        prev.disabled = currentPage === 1;
        prev.addEventListener('click', function () { currentPage--; render(); });
        paginationEl.appendChild(prev);

        for (var i = 1; i <= totalPages; i++) {
            (function (page) {
                var btn = document.createElement('button');
                btn.className = 'page-btn' + (page === currentPage ? ' active' : '');
                btn.textContent = page;
                btn.addEventListener('click', function () { currentPage = page; render(); });
                paginationEl.appendChild(btn);
            })(i);
        }

        var next = document.createElement('button');
        next.className = 'page-btn';
        next.textContent = '다음';
        next.disabled = currentPage === totalPages;
        next.addEventListener('click', function () { currentPage++; render(); });
        paginationEl.appendChild(next);
    }

    render();
}

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