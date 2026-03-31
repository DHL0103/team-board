class MyPage {

    static init() {
        MyPage.#initSidebarTabs();
        MyPage.#initPagination();
        new PasswordValidator('newPassword', 'newPasswordConfirm', 'mypage-input-error-msg', '새 비밀번호가 일치하지 않습니다.');
    }

    static #initSidebarTabs() {
        const sidebarItems = document.querySelectorAll('.sidebar-item');
        const sections     = document.querySelectorAll('.mypage-section');

        sidebarItems.forEach(item => {
            item.addEventListener('click', () => {
                const target = item.dataset.section;
                sidebarItems.forEach(i => i.classList.remove('active'));
                sections.forEach(s => s.classList.remove('active'));
                item.classList.add('active');
                document.getElementById('section-' + target).classList.add('active');
            });
        });
    }

    static #initPagination() {
        const PAGE_SIZE = 10;

        const assignedList       = document.getElementById('assigned-list');
        const assignedPagination = document.getElementById('assigned-pagination');
        if (assignedList && assignedPagination) {
            const assignedItems = Array.from(assignedList.querySelectorAll('.assigned-card'));
            new Pagination(assignedItems, assignedPagination, PAGE_SIZE);
        }

        const inviteList       = document.getElementById('invite-list');
        const invitePagination = document.getElementById('invite-pagination');
        if (inviteList && invitePagination) {
            const inviteItems = Array.from(inviteList.children);
            new Pagination(inviteItems, invitePagination, PAGE_SIZE);
        }
    }
}

document.addEventListener('DOMContentLoaded', () => MyPage.init());
