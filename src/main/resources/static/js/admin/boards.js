const PAGE_SIZE = 10;

const tbody = document.getElementById("board-tbody");
const searchInput = document.getElementById("board-search");
const paginationWrap = document.getElementById("pagination-wrap");

if (tbody) {
    const allRows = Array.from(tbody.querySelectorAll("tr"));
    let filteredRows = allRows;
    let currentPage = 1;

    function render() {
        const start = (currentPage - 1) * PAGE_SIZE;
        const end = start + PAGE_SIZE;
        allRows.forEach(row => { row.style.display = "none"; });
        filteredRows.slice(start, end).forEach(row => { row.style.display = ""; });
        const totalPages = Math.max(1, Math.ceil(filteredRows.length / PAGE_SIZE));
        App.renderPagination(paginationWrap, totalPages, currentPage, function (page) {
            currentPage = page;
            render();
        });
    }

    searchInput.addEventListener("input", () => {
        const query = searchInput.value.trim().toLowerCase();
        filteredRows = allRows.filter(row => row.dataset.name.toLowerCase().includes(query));
        currentPage = 1;
        render();
    });

    render();

    tbody.addEventListener("click", e => {
        if (e.target.closest(".status-badge--toggle")) {
            return;
        }
        const row = e.target.closest("tr[data-board-id]");
        if (row) {
            window.location.href = "/board/" + row.dataset.boardId;
        }
    });
}

// ── 보드 상태 변경 ──
const statusChangeModal = document.getElementById("statusChangeModal");
const statusChangeDesc = document.getElementById("statusChangeDesc");
const statusChangeConfirm = document.getElementById("statusChangeConfirm");
const statusChangeCancel = document.getElementById("statusChangeCancel");

let pendingStatusChange = null;

if (statusChangeModal) {
    document.getElementById("board-tbody").addEventListener("click", e => {
        const badge = e.target.closest(".status-badge--toggle");
        if (!badge) { return; }
        e.stopPropagation();

        const boardId = badge.dataset.boardId;
        const currentStatus = badge.dataset.currentStatus;
        const nextStatus = currentStatus === "ACTIVE" ? "INACTIVE" : "ACTIVE";
        const nextLabel = nextStatus === "ACTIVE" ? "활성" : "비활성";

        pendingStatusChange = { boardId, nextStatus, badge };
        statusChangeDesc.textContent = `이 보드를 "${nextLabel}" 상태로 변경하시겠습니까?`;
        statusChangeModal.classList.add("open");
    });

    statusChangeCancel.addEventListener("click", () => {
        statusChangeModal.classList.remove("open");
        pendingStatusChange = null;
    });

    statusChangeModal.addEventListener("click", e => {
        if (e.target === statusChangeModal) {
            statusChangeModal.classList.remove("open");
            pendingStatusChange = null;
        }
    });

    statusChangeConfirm.addEventListener("click", async () => {
        if (!pendingStatusChange) { return; }
        const { boardId, nextStatus, badge } = pendingStatusChange;
        statusChangeModal.classList.remove("open");
        pendingStatusChange = null;

        try {
            const res = await fetch(`/admin/api/boards/${boardId}/status?status=${nextStatus}`, { method: "POST" });
            if (!res.ok) { return; }

            const isActive = nextStatus === "ACTIVE";
            badge.textContent = isActive ? "활성" : "비활성";
            badge.className = "status-badge status-badge--toggle " + (isActive ? "status-active" : "status-inactive");
            badge.dataset.currentStatus = nextStatus;
        } catch (err) {
            console.error(err);
        }
    });
}
