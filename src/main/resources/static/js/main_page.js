// ── 기본 초기화 ──
const colMap = {};
document.querySelectorAll(".kanban-col[data-board-id]").forEach(col => {
    const id = col.dataset.boardId;
    colMap[id] = { bodyId: `body-${id}`, countId: `count-${id}`, emptyId: `empty-${id}` };
});

Object.values(colMap).forEach(({ bodyId, countId, emptyId }) => {
    const body = document.getElementById(bodyId);
    if (!body) return;
    const cards = body.querySelectorAll(".board-card");
    let visible = 0;
    cards.forEach(card => { if (card.dataset.status === "COMPLETED") card.style.display = "none"; else visible++; });
    document.getElementById(countId).textContent = visible + "개";
    if (emptyId && visible === 0) document.getElementById(emptyId).style.display = "flex";
});

document.querySelectorAll(".status-chip").forEach(chip => {
    const s = chip.dataset.status;
    if (s === "PROGRESS")  chip.classList.add("progress");
    if (s === "REQUESTED") chip.classList.add("requested");
    if (s === "REJECTED")  chip.classList.add("rejected");
});

document.querySelectorAll(".board-card").forEach(card => {
    card.addEventListener("mouseenter", () => card.classList.add("is-hovered"));
    card.addEventListener("mouseleave", () => card.classList.remove("is-hovered"));
    card.addEventListener("click", () => {
        const postId = card.dataset.id;
        if (postId) location.href = `/post/${postId}`;
    });
});

document.querySelectorAll(".board-card").forEach(async card => {
    if (card.dataset.status === "COMPLETED") return;
    const memberId = card.dataset.memberId;
    if (!memberId) return;
    const name = await App.fetchUsername(memberId);
    card.querySelector(".writer-name").textContent = name;
});

document.querySelectorAll(".view-tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".view-tab").forEach(t => t.classList.remove("active"));
        tab.classList.add("active");
    });
});

// ── 보드 검색 + 활성/비활성 필터 ──
const allBoardCards = Array.from(document.querySelectorAll(".board-grid-card[data-status]"));
const boardCountLabel = document.getElementById("board-count-label");
const btnBoardFilterActive = document.getElementById("btn-board-filter-active");
const btnBoardFilterInactive = document.getElementById("btn-board-filter-inactive");
const boardSearchInput = document.getElementById("boardSearchInput");

let currentFilterStatus = "ACTIVE";

function applyFilters() {
    const query = boardSearchInput ? boardSearchInput.value.trim().toLowerCase() : "";
    let count = 0;
    allBoardCards.forEach(card => {
        const matchesStatus = card.dataset.status === currentFilterStatus;
        const nameEl = card.querySelector(".board-grid-card-name");
        const matchesSearch = !query || (nameEl && nameEl.textContent.toLowerCase().includes(query));
        const visible = matchesStatus && matchesSearch;
        card.style.display = visible ? "" : "none";
        if (visible) { count++; }
    });
    boardCountLabel.textContent = `총 ${count}개의 보드`;
}

if (boardSearchInput) {
    boardSearchInput.addEventListener("input", applyFilters);
}

if (btnBoardFilterActive) {
    btnBoardFilterActive.addEventListener("click", () => {
        btnBoardFilterActive.classList.add("active");
        btnBoardFilterInactive.classList.remove("active");
        currentFilterStatus = "ACTIVE";
        applyFilters();
    });
}

if (btnBoardFilterInactive) {
    btnBoardFilterInactive.addEventListener("click", () => {
        btnBoardFilterInactive.classList.add("active");
        btnBoardFilterActive.classList.remove("active");
        currentFilterStatus = "INACTIVE";
        applyFilters();
    });
}

applyFilters();

// ── 보드 생성 모달 ──
const createBoardModal = document.getElementById("createBoardModal");
const btnOpenBoardCreate = document.getElementById("btn-open-board-create");

if (createBoardModal && btnOpenBoardCreate) {
    btnOpenBoardCreate.addEventListener("click", () => {
        App.openModal("createBoardModal");
    });

    const closeBoard = () => { App.closeModal("createBoardModal"); };

    document.getElementById("btn-close-board-create").addEventListener("click", closeBoard);
    document.getElementById("btn-cancel-board-create").addEventListener("click", closeBoard);

    App.initCharCounter(
        document.getElementById("createBoardName"),
        document.getElementById("createBoardNameCount"),
        50, 45
    );
}

