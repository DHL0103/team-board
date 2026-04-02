class AdminMembersPage {

    static #initialized = false;
    static #PAGE_SIZE = 10;
    static #ROLE_LABEL = { MANAGER: 'Manager', USER: 'User', REQUESTED: '가입 요청', INVITED: '초대됨' };
    static #ROLE_CLASS = { MANAGER: 'role-manager', USER: 'role-user', REQUESTED: 'role-requested', INVITED: 'role-invited' };
    static #ROLE_BADGE = {
        ROLE_ADMIN:     '<span class="role-badge role-admin">Admin</span>',
        ROLE_USER:      '<span class="role-badge role-user">User</span>',
        ROLE_SUSPENDED: '<span class="role-badge role-suspended">정지</span>',
    };

    static #currentPage = 1;
    static #currentFilter = 'active';
    static #debounceTimer = null;
    static #currentDetailRow = null;

    static #tbody;
    static #memberTable;
    static #memberEmpty;
    static #countMeta;
    static #paginationWrap;
    static #modalBoardList;
    static #btnRoleAction;

    static init() {
        if (AdminMembersPage.#initialized) { return; }
        AdminMembersPage.#initialized = true;

        AdminMembersPage.#tbody          = document.getElementById('member-tbody');
        AdminMembersPage.#memberTable    = document.getElementById('member-table');
        AdminMembersPage.#memberEmpty    = document.getElementById('member-empty');
        AdminMembersPage.#countMeta      = document.getElementById('member-count-meta');
        AdminMembersPage.#paginationWrap = document.getElementById('pagination-wrap');
        AdminMembersPage.#modalBoardList = document.getElementById('modal-board-list');
        AdminMembersPage.#btnRoleAction  = document.getElementById('btn-member-role-action');

        if (!AdminMembersPage.#tbody) { return; }

        AdminMembersPage.#initFilters();
        AdminMembersPage.#initSearch();
        AdminMembersPage.#initDetailModal();
        AdminMembersPage.#fetchAndRender();
    }

    static #createRow(member, index) {
        const tr = document.createElement('tr');
        tr.className = 'admin-table-row';
        tr.dataset.memberId = member.id;
        tr.dataset.username = member.username;
        tr.dataset.role     = member.role;
        tr.innerHTML =
            `<td class="row-num">${(AdminMembersPage.#currentPage - 1) * AdminMembersPage.#PAGE_SIZE + index + 1}</td>` +
            `<td>${member.username}</td>` +
            `<td>${AdminMembersPage.#ROLE_BADGE[member.role] ?? member.role}</td>`;
        return tr;
    }

    static async #fetchAndRender() {
        const searchInput = document.getElementById('member-search');
        const params = new URLSearchParams({
            username: searchInput ? searchInput.value.trim() : '',
            filter: AdminMembersPage.#currentFilter,
            page:   AdminMembersPage.#currentPage - 1,
            size:   AdminMembersPage.#PAGE_SIZE,
        });

        try {
            const res  = await fetch(`/api/admin/members?${params}`);
            const data = await res.json();

            if (AdminMembersPage.#countMeta) { AdminMembersPage.#countMeta.textContent = data.totalCount + '명'; }

            AdminMembersPage.#tbody.innerHTML = '';

            if (data.members.length === 0) {
                AdminMembersPage.#memberEmpty.style.display = '';
                AdminMembersPage.#memberTable.style.display = 'none';
            } else {
                AdminMembersPage.#memberEmpty.style.display = 'none';
                AdminMembersPage.#memberTable.style.display = '';
                data.members.forEach((member, i) => AdminMembersPage.#tbody.appendChild(AdminMembersPage.#createRow(member, i)));
            }

            const totalPages = Math.max(1, Math.ceil(data.totalCount / AdminMembersPage.#PAGE_SIZE));
            Pagination.render(AdminMembersPage.#paginationWrap, totalPages, AdminMembersPage.#currentPage, page => {
                AdminMembersPage.#currentPage = page;
                AdminMembersPage.#fetchAndRender();
            });
        } catch (e) {
            console.error('멤버 목록 로드 실패', e);
        }
    }

    static #initFilters() {
        const btnFilterActive    = document.getElementById('btn-filter-active');
        const btnFilterSuspended = document.getElementById('btn-filter-suspended');

        if (btnFilterActive) {
            btnFilterActive.addEventListener('click', () => {
                AdminMembersPage.#currentFilter = 'active';
                AdminMembersPage.#currentPage = 1;
                btnFilterActive.classList.add('active');
                btnFilterSuspended.classList.remove('active');
                AdminMembersPage.#fetchAndRender();
            });
        }
        if (btnFilterSuspended) {
            btnFilterSuspended.addEventListener('click', () => {
                AdminMembersPage.#currentFilter = 'suspended';
                AdminMembersPage.#currentPage = 1;
                btnFilterSuspended.classList.add('active');
                btnFilterActive.classList.remove('active');
                AdminMembersPage.#fetchAndRender();
            });
        }
    }

    static #initSearch() {
        const searchInput = document.getElementById('member-search');
        if (searchInput) {
            searchInput.addEventListener('input', () => {
                clearTimeout(AdminMembersPage.#debounceTimer);
                AdminMembersPage.#debounceTimer = setTimeout(() => {
                    AdminMembersPage.#currentPage = 1;
                    AdminMembersPage.#fetchAndRender();
                }, 250);
            });
        }
    }

    static #initDetailModal() {
        const detailModal   = document.getElementById('memberDetailModal');
        const modalUsername  = document.getElementById('modal-member-username');
        const btnClose      = document.getElementById('btn-close-member-detail');

        if (AdminMembersPage.#tbody) {
            AdminMembersPage.#tbody.addEventListener('click', e => {
                const row = e.target.closest('tr[data-member-id]');
                if (row) { AdminMembersPage.#openDetailModal(row, modalUsername); }
            });
        }

        if (btnClose) {
            btnClose.addEventListener('click', () => AdminMembersPage.#closeDetailModal());
        }

        if (detailModal) {
            detailModal.addEventListener('click', e => {
                if (e.target === detailModal) { AdminMembersPage.#closeDetailModal(); }
            });
        }

        document.addEventListener('keydown', e => {
            if (e.key === 'Escape' && detailModal && detailModal.classList.contains('open')) {
                AdminMembersPage.#closeDetailModal();
            }
        });

        if (AdminMembersPage.#btnRoleAction) {
            AdminMembersPage.#btnRoleAction.addEventListener('click', () => AdminMembersPage.#changeRole());
        }
    }

    static #openDetailModal(row, modalUsername) {
        const memberId = row.dataset.memberId;
        const username = row.dataset.username;
        const role     = row.dataset.role;

        AdminMembersPage.#currentDetailRow = row;
        modalUsername.textContent = username;
        AdminMembersPage.#modalBoardList.innerHTML = '<div class="admin-empty">불러오는 중...</div>';

        const btn = AdminMembersPage.#btnRoleAction;
        if (role === 'ROLE_USER') {
            btn.textContent = '정지하기';
            btn.className   = 'btn-admin-danger';
            btn.style.display = '';
        } else if (role === 'ROLE_SUSPENDED') {
            btn.textContent = '복구하기';
            btn.className   = 'btn-admin-edit';
            btn.style.display = '';
        } else {
            btn.style.display = 'none';
        }

        Modal.open('memberDetailModal');

        fetch(`/api/admin/members/${memberId}/boards`)
            .then(res => res.json())
            .then(boards => {
                if (boards.length === 0) {
                    AdminMembersPage.#modalBoardList.innerHTML = '<div class="admin-empty">소속 보드가 없습니다.</div>';
                    return;
                }
                const rows = boards.map(b => {
                    const label = AdminMembersPage.#ROLE_LABEL[b.boardRole] ?? b.boardRole;
                    const cls   = AdminMembersPage.#ROLE_CLASS[b.boardRole] ?? '';
                    return `<tr>
                        <td><a href="/boards/${b.boardId}" class="admin-board-link">${b.boardName}</a></td>
                        <td><span class="role-badge ${cls}">${label}</span></td>
                    </tr>`;
                }).join('');
                AdminMembersPage.#modalBoardList.innerHTML = `<table class="admin-table">
                    <thead><tr><th>보드</th><th style="width:110px;">역할</th></tr></thead>
                    <tbody>${rows}</tbody>
                </table>`;
            })
            .catch(() => {
                AdminMembersPage.#modalBoardList.innerHTML = '<div class="admin-empty">불러오기 실패</div>';
            });
    }

    static #closeDetailModal() {
        Modal.close('memberDetailModal');
        AdminMembersPage.#currentDetailRow = null;
    }

    static #changeRole() {
        if (!AdminMembersPage.#currentDetailRow) { return; }
        const memberId    = AdminMembersPage.#currentDetailRow.dataset.memberId;
        const currentRole = AdminMembersPage.#currentDetailRow.dataset.role;
        const newRole     = currentRole === 'ROLE_USER' ? 'ROLE_SUSPENDED' : 'ROLE_USER';

        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
        const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
        const headers = {};
        if (csrfHeader && csrfToken) { headers[csrfHeader] = csrfToken; }
        fetch(`/api/admin/members/${memberId}/role?role=${newRole}`, { method: 'POST', headers })
            .then(res => {
                if (!res.ok) { return; }
                AdminMembersPage.#closeDetailModal();
                AdminMembersPage.#fetchAndRender();
            });
    }
}

document.addEventListener('DOMContentLoaded', () => AdminMembersPage.init());
