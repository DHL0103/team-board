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

