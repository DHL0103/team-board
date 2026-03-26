// ── 보드 목록 fetch ──
const boardGrid = document.getElementById("board-grid");
const boardCountLabel = document.getElementById("board-count-label");
const btnBoardFilterActive = document.getElementById("btn-board-filter-active");
const btnBoardFilterInactive = document.getElementById("btn-board-filter-inactive");
const boardSearchInput = document.getElementById("boardSearchInput");

let currentStatus = "ACTIVE";
let searchTimer = null;

function createBoardCard(board) {
    const a = document.createElement("a");
    a.className = `board-grid-card palette-${board.color}`;
    a.href = `/board/${board.id}`;
    a.innerHTML =
        '<div class="board-grid-card-bar"></div>' +
        '<div class="board-grid-card-body">' +
            '<div class="board-grid-card-name"></div>' +
            '<div class="board-grid-card-footer">' +
                '<span class="board-grid-member-count">' +
                    '<svg width="11" height="11"><use href="/img/icons.svg#icon-user"></use></svg>' +
                    '<span class="member-count-label"></span>' +
                '</span>' +
            '</div>' +
        '</div>';
    a.querySelector(".board-grid-card-name").textContent = board.name;
    a.querySelector(".member-count-label").textContent = board.memberCount;
    return a;
}

async function loadBoards(status, keyword) {
    const params = new URLSearchParams({ status, q: keyword || "" });
    const res = await fetch(`/api/boards?${params}`);
    const boards = await res.json();

    boardGrid.querySelectorAll(".board-grid-card").forEach(el => el.remove());
    boards.forEach(board => boardGrid.appendChild(createBoardCard(board)));
    boardCountLabel.textContent = `총 ${boards.length}개의 보드`;
}

if (boardSearchInput) {
    boardSearchInput.addEventListener("input", () => {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => loadBoards(currentStatus, boardSearchInput.value.trim()), 300);
    });
}

if (btnBoardFilterActive) {
    btnBoardFilterActive.addEventListener("click", () => {
        btnBoardFilterActive.classList.add("active");
        btnBoardFilterInactive.classList.remove("active");
        currentStatus = "ACTIVE";
        loadBoards(currentStatus, boardSearchInput ? boardSearchInput.value.trim() : "");
    });
}

if (btnBoardFilterInactive) {
    btnBoardFilterInactive.addEventListener("click", () => {
        btnBoardFilterInactive.classList.add("active");
        btnBoardFilterActive.classList.remove("active");
        currentStatus = "INACTIVE";
        loadBoards(currentStatus, boardSearchInput ? boardSearchInput.value.trim() : "");
    });
}

loadBoards("ACTIVE", "");

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

