class AdminBoardsPage {

    static #initialized = false;
    static #PAGE_SIZE = 10;
    static #allRows = [];
    static #filteredRows = [];
    static #currentPage = 1;
    static #pendingStatusChange = null;

    static init() {
        if (AdminBoardsPage.#initialized) { return; }
        AdminBoardsPage.#initialized = true;
        AdminBoardsPage.#initTable();
        AdminBoardsPage.#initStatusChangeModal();
    }

    static #initTable() {
        const tbody        = document.getElementById('board-tbody');
        const searchInput  = document.getElementById('board-search');
        const paginationWrap = document.getElementById('pagination-wrap');
        if (!tbody) { return; }

        AdminBoardsPage.#allRows      = Array.from(tbody.querySelectorAll('tr'));
        AdminBoardsPage.#filteredRows = AdminBoardsPage.#allRows;

        searchInput.addEventListener('input', () => {
            const query = searchInput.value.trim().toLowerCase();
            AdminBoardsPage.#filteredRows = AdminBoardsPage.#allRows.filter(row =>
                row.dataset.name.toLowerCase().includes(query)
            );
            AdminBoardsPage.#currentPage = 1;
            AdminBoardsPage.#render(paginationWrap);
        });

        tbody.addEventListener('click', e => {
            if (e.target.closest('.status-badge--toggle')) { return; }
            const row = e.target.closest('tr[data-board-id]');
            if (row) { window.location.href = '/board/' + row.dataset.boardId; }
        });

        AdminBoardsPage.#render(paginationWrap);
    }

    static #render(paginationWrap) {
        const start = (AdminBoardsPage.#currentPage - 1) * AdminBoardsPage.#PAGE_SIZE;
        const end   = start + AdminBoardsPage.#PAGE_SIZE;
        AdminBoardsPage.#allRows.forEach(row => { row.style.display = 'none'; });
        AdminBoardsPage.#filteredRows.slice(start, end).forEach(row => { row.style.display = ''; });
        const totalPages = Math.max(1, Math.ceil(AdminBoardsPage.#filteredRows.length / AdminBoardsPage.#PAGE_SIZE));
        Pagination.render(paginationWrap, totalPages, AdminBoardsPage.#currentPage, page => {
            AdminBoardsPage.#currentPage = page;
            AdminBoardsPage.#render(paginationWrap);
        });
    }

    static #initStatusChangeModal() {
        const modal        = document.getElementById('statusChangeModal');
        const descEl       = document.getElementById('statusChangeDesc');
        const confirmBtn   = document.getElementById('statusChangeConfirm');
        const cancelBtn    = document.getElementById('statusChangeCancel');
        if (!modal) { return; }

        document.getElementById('board-tbody').addEventListener('click', e => {
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
                const res = await fetch(`/admin/api/boards/${boardId}/status?status=${nextStatus}`, { method: 'POST' });
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
