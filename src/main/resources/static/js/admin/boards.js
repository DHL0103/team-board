const PAGE_SIZE = 10;

const tbody        = document.getElementById("board-tbody");
const boardTable   = document.getElementById("board-table");
const boardEmpty   = document.getElementById("board-empty");
const countMeta    = document.getElementById("board-count-meta");
const searchInput  = document.getElementById("board-search");
const paginationWrap = document.getElementById("pagination-wrap");

let currentPage   = 1;
let debounceTimer = null;

function createRow(board) {
    const tr = document.createElement("tr");
    tr.className = "admin-table-row";
    tr.dataset.boardId = board.id;
    tr.dataset.name    = board.name;

    const isActive = board.status === "ACTIVE";
    tr.innerHTML =
        `<td><span class="board-color-dot dot-${board.color}"></span></td>` +
        `<td><a href="/board/${board.id}" class="admin-board-link">${board.name}</a></td>` +
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

async function fetchAndRender() {
    const params = new URLSearchParams({
        q:    searchInput ? searchInput.value.trim() : "",
        status: "",
        sort:   "recent",
        page:   currentPage - 1,
        size:   PAGE_SIZE,
    });

    try {
        const res  = await fetch(`/api/boards/search?${params}`);
        const data = await res.json();

        if (countMeta) { countMeta.textContent = data.totalCount + "개"; }

        tbody.innerHTML = "";

        if (data.boards.length === 0) {
            boardEmpty.style.display = "";
            boardTable.style.display = "none";
        } else {
            boardEmpty.style.display = "none";
            boardTable.style.display = "";
            data.boards.forEach(board => tbody.appendChild(createRow(board)));
        }

        const totalPages = Math.max(1, Math.ceil(data.totalCount / PAGE_SIZE));
        App.renderPagination(paginationWrap, totalPages, currentPage, page => {
            currentPage = page;
            fetchAndRender();
        });
    } catch (e) {
        console.error("보드 목록 로드 실패", e);
    }
}

if (searchInput) {
    searchInput.addEventListener("input", () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            currentPage = 1;
            fetchAndRender();
        }, 250);
    });
}

tbody.addEventListener("click", e => {
    if (e.target.closest(".status-badge--toggle")) { return; }
    const row = e.target.closest("tr[data-board-id]");
    if (row) { window.location.href = "/board/" + row.dataset.boardId; }
});

fetchAndRender();

// ── 보드 상태 변경 ──
const statusChangeModal   = document.getElementById("statusChangeModal");
const statusChangeDesc    = document.getElementById("statusChangeDesc");
const statusChangeConfirm = document.getElementById("statusChangeConfirm");
const statusChangeCancel  = document.getElementById("statusChangeCancel");

let pendingStatusChange = null;

if (statusChangeModal) {
    tbody.addEventListener("click", e => {
        const badge = e.target.closest(".status-badge--toggle");
        if (!badge) { return; }
        e.stopPropagation();

        const boardId       = badge.dataset.boardId;
        const currentStatus = badge.dataset.currentStatus;
        const nextStatus    = currentStatus === "ACTIVE" ? "INACTIVE" : "ACTIVE";
        const nextLabel     = nextStatus === "ACTIVE" ? "활성" : "비활성";

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
            const res = await fetch(`/api/admin/boards/${boardId}/status?status=${nextStatus}`, { method: "POST" });
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
