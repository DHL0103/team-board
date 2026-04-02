class AdminBoardsPage {

    static #initialized = false;
    static #PAGE_SIZE = 10;
    static #currentPage = 1;
    static #debounceTimer = null;
    static #pendingStatusChange = null;

    static #tbody;
    static #boardTable;
    static #boardEmpty;
    static #countMeta;
    static #paginationWrap;

    static init() {
        if (AdminBoardsPage.#initialized) { return; }
        AdminBoardsPage.#initialized = true;

        AdminBoardsPage.#tbody          = document.getElementById('board-tbody');
        AdminBoardsPage.#boardTable     = document.getElementById('board-table');
        AdminBoardsPage.#boardEmpty     = document.getElementById('board-empty');
        AdminBoardsPage.#countMeta      = document.getElementById('board-count-meta');
        AdminBoardsPage.#paginationWrap = document.getElementById('pagination-wrap');

        if (!AdminBoardsPage.#tbody) { return; }

        AdminBoardsPage.#initSearch();
        AdminBoardsPage.#initRowClick();
        AdminBoardsPage.#initStatusChangeModal();
        AdminBoardsPage.#fetchAndRender();
    }

    static #createRow(board) {
        const tr = document.createElement('tr');
        tr.className = 'admin-table-row';
        tr.dataset.boardId = board.id;
        tr.dataset.name    = board.name;

        const isActive = board.status === 'ACTIVE';
        tr.innerHTML =
            `<td><span class="board-color-dot dot-${board.color}"></span></td>` +
            `<td><a href="/boards/${board.id}" class="admin-board-link">${board.name}</a></td>` +
            `<td style="color:var(--text-sub); max-width:300px; font-size:12px; word-break:break-word;">${board.description ?? '-'}</td>` +
            `<td>${board.memberCount}</td>` +
            `<td style="text-align:center;">` +
                `<span class="status-badge status-badge--toggle ${isActive ? 'status-active' : 'status-inactive'}"` +
                      ` data-board-id="${board.id}" data-current-status="${board.status}">` +
                      `${isActive ? '활성' : '비활성'}` +
                `</span>` +
            `</td>`;
        return tr;
    }

    static async #fetchAndRender() {
        const searchInput = document.getElementById('board-search');
        const params = new URLSearchParams({
            boardName:   searchInput ? searchInput.value.trim() : '',
            boardStatus: '',
            sort:        'recent',
            page:   AdminBoardsPage.#currentPage - 1,
            size:   AdminBoardsPage.#PAGE_SIZE,
        });

        try {
            const res  = await fetch(`/api/boards/search?${params}`);
            const data = await res.json();

            if (AdminBoardsPage.#countMeta) { AdminBoardsPage.#countMeta.textContent = data.totalCount + '개'; }

            AdminBoardsPage.#tbody.innerHTML = '';

            if (data.boards.length === 0) {
                AdminBoardsPage.#boardEmpty.style.display = '';
                AdminBoardsPage.#boardTable.style.display = 'none';
            } else {
                AdminBoardsPage.#boardEmpty.style.display = 'none';
                AdminBoardsPage.#boardTable.style.display = '';
                data.boards.forEach(board => AdminBoardsPage.#tbody.appendChild(AdminBoardsPage.#createRow(board)));
            }

            const totalPages = Math.max(1, Math.ceil(data.totalCount / AdminBoardsPage.#PAGE_SIZE));
            Pagination.render(AdminBoardsPage.#paginationWrap, totalPages, AdminBoardsPage.#currentPage, page => {
                AdminBoardsPage.#currentPage = page;
                AdminBoardsPage.#fetchAndRender();
            });
        } catch (e) {
            console.error('보드 목록 로드 실패', e);
        }
    }

    static #initSearch() {
        const searchInput = document.getElementById('board-search');
        if (searchInput) {
            searchInput.addEventListener('input', () => {
                clearTimeout(AdminBoardsPage.#debounceTimer);
                AdminBoardsPage.#debounceTimer = setTimeout(() => {
                    AdminBoardsPage.#currentPage = 1;
                    AdminBoardsPage.#fetchAndRender();
                }, 250);
            });
        }
    }

    static #initRowClick() {
        AdminBoardsPage.#tbody.addEventListener('click', e => {
            if (e.target.closest('.status-badge--toggle')) { return; }
            const row = e.target.closest('tr[data-board-id]');
            if (row) { window.location.href = '/boards/' + row.dataset.boardId; }
        });
    }

    static #initStatusChangeModal() {
        const modal      = document.getElementById('statusChangeModal');
        const descEl     = document.getElementById('statusChangeDesc');
        const confirmBtn = document.getElementById('statusChangeConfirm');
        const cancelBtn  = document.getElementById('statusChangeCancel');
        if (!modal) { return; }

        AdminBoardsPage.#tbody.addEventListener('click', e => {
            const badge = e.target.closest('.status-badge--toggle');
            if (!badge) { return; }
            e.stopPropagation();

            const boardId       = badge.dataset.boardId;
            const currentStatus = badge.dataset.currentStatus;
            const nextStatus    = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
            const nextLabel     = nextStatus === 'ACTIVE' ? '활성' : '비활성';

            AdminBoardsPage.#pendingStatusChange = { boardId, nextStatus, badge };
            descEl.textContent = `이 보드를 "${nextLabel}" 상태로 변경하시겠습니까?`;
            modal.classList.add('open');
        });

        cancelBtn.addEventListener('click', () => {
            modal.classList.remove('open');
            AdminBoardsPage.#pendingStatusChange = null;
        });

        modal.addEventListener('click', e => {
            if (e.target === modal) {
                modal.classList.remove('open');
                AdminBoardsPage.#pendingStatusChange = null;
            }
        });

        confirmBtn.addEventListener('click', async () => {
            if (!AdminBoardsPage.#pendingStatusChange) { return; }
            const { boardId, nextStatus, badge } = AdminBoardsPage.#pendingStatusChange;
            modal.classList.remove('open');
            AdminBoardsPage.#pendingStatusChange = null;

            try {
                const res = await fetch(`/api/admin/boards/${boardId}/status?status=${nextStatus}`, { method: 'POST' });
                if (!res.ok) { return; }

                const isActive = nextStatus === 'ACTIVE';
                badge.textContent = isActive ? '활성' : '비활성';
                badge.className = 'status-badge status-badge--toggle ' + (isActive ? 'status-active' : 'status-inactive');
                badge.dataset.currentStatus = nextStatus;
            } catch (err) {
                console.error(err);
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => AdminBoardsPage.init());
