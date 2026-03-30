class AdminMembersPage {

    static #PAGE_SIZE = 10;
    static #ROLE_LABEL = { MANAGER: 'Manager', USER: 'User', REQUESTED: '가입 요청', INVITED: '초대됨' };
    static #ROLE_CLASS  = { MANAGER: 'role-manager', USER: 'role-user', REQUESTED: 'role-requested', INVITED: 'role-invited' };

    static #allRows = [];
    static #filteredRows = [];
    static #currentPage = 1;
    static #currentFilter = 'active';
    static #currentDetailRow = null;

    static #tbody;
    static #searchInput;
    static #paginationWrap;
    static #modalBoardList;
    static #btnRoleAction;

    static init() {
        AdminMembersPage.#tbody        = document.getElementById('member-tbody');
        AdminMembersPage.#searchInput  = document.getElementById('member-search');
        AdminMembersPage.#paginationWrap = document.getElementById('pagination-wrap');
        AdminMembersPage.#modalBoardList = document.getElementById('modal-board-list');
        AdminMembersPage.#btnRoleAction  = document.getElementById('btn-member-role-action');

        if (!AdminMembersPage.#tbody) { return; }

        AdminMembersPage.#allRows = Array.from(AdminMembersPage.#tbody.querySelectorAll('tr'));
        AdminMembersPage.#initFilters();
        AdminMembersPage.#initDetailModal();
        AdminMembersPage.#applyFilters();
    }

    static #initFilters() {
        const btnFilterActive    = document.getElementById('btn-filter-active');
        const btnFilterSuspended = document.getElementById('btn-filter-suspended');

        if (btnFilterActive) {
            btnFilterActive.addEventListener('click', () => {
                AdminMembersPage.#currentFilter = 'active';
                btnFilterActive.classList.add('active');
                btnFilterSuspended.classList.remove('active');
                AdminMembersPage.#applyFilters();
            });
        }
        if (btnFilterSuspended) {
            btnFilterSuspended.addEventListener('click', () => {
                AdminMembersPage.#currentFilter = 'suspended';
                btnFilterSuspended.classList.add('active');
                btnFilterActive.classList.remove('active');
                AdminMembersPage.#applyFilters();
            });
        }

        AdminMembersPage.#searchInput.addEventListener('input', () => AdminMembersPage.#applyFilters());
    }

    static #applyFilters() {
        const query = AdminMembersPage.#searchInput.value.trim().toLowerCase();
        AdminMembersPage.#filteredRows = AdminMembersPage.#allRows.filter(row => {
            const matchesFilter = AdminMembersPage.#currentFilter === 'active'
                ? row.dataset.role !== 'ROLE_SUSPENDED'
                : row.dataset.role === 'ROLE_SUSPENDED';
            return matchesFilter && row.dataset.username.toLowerCase().includes(query);
        });
        AdminMembersPage.#currentPage = 1;
        AdminMembersPage.#render();
    }

    static #render() {
        const start = (AdminMembersPage.#currentPage - 1) * AdminMembersPage.#PAGE_SIZE;
        const end   = start + AdminMembersPage.#PAGE_SIZE;
        AdminMembersPage.#allRows.forEach(row => { row.style.display = 'none'; });
        AdminMembersPage.#filteredRows.slice(start, end).forEach((row, i) => {
            row.style.display = '';
            const numCell = row.querySelector('.row-num');
            if (numCell) { numCell.textContent = start + i + 1; }
        });
        const totalPages = Math.max(1, Math.ceil(AdminMembersPage.#filteredRows.length / AdminMembersPage.#PAGE_SIZE));
        Pagination.render(AdminMembersPage.#paginationWrap, totalPages, AdminMembersPage.#currentPage, page => {
            AdminMembersPage.#currentPage = page;
            AdminMembersPage.#render();
        });
    }

    static #initDetailModal() {
        const detailModal   = document.getElementById('memberDetailModal');
        const modalUsername = document.getElementById('modal-member-username');
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

        fetch(`/admin/api/members/${memberId}/boards`)
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
                        <td><a href="/board/${b.boardId}" class="admin-board-link">${b.boardName}</a></td>
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
        const row     = AdminMembersPage.#currentDetailRow;
        const memberId    = row.dataset.memberId;
        const currentRole = row.dataset.role;
        const newRole     = currentRole === 'ROLE_USER' ? 'ROLE_SUSPENDED' : 'ROLE_USER';

        fetch(`/admin/api/members/${memberId}/role?role=${newRole}`, { method: 'POST' })
            .then(res => {
                if (!res.ok) { return; }
                row.dataset.role = newRole;
                const badge = row.querySelector('.role-badge');
                if (newRole === 'ROLE_SUSPENDED') {
                    badge.className = 'role-badge role-suspended';
                    badge.textContent = '정지';
                } else {
                    badge.className = 'role-badge role-user';
                    badge.textContent = 'User';
                }
                AdminMembersPage.#closeDetailModal();
                AdminMembersPage.#applyFilters();
            });
    }
}

document.addEventListener('DOMContentLoaded', () => AdminMembersPage.init());
