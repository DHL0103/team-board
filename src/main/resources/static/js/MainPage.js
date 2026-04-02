class MainPage {

    static #initialized = false;
    static #currentStatus = 'ACTIVE';
    static #searchTimer = null;

    static init() {
        if (MainPage.#initialized) { return; }
        MainPage.#initialized = true;

        MainPage.#initBoardFilter();
        MainPage.#initCreateBoardModal();
    }

    static #createBoardCard(board) {
        const a = document.createElement('a');
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
        a.querySelector('.board-grid-card-name').textContent = board.name;
        a.querySelector('.member-count-label').textContent = board.memberCount;
        return a;
    }

    static async #loadBoards(status, boardName) {
        const boardGrid      = document.getElementById('board-grid');
        const boardCountLabel = document.getElementById('board-count-label');

        const params = new URLSearchParams({ status, boardName: boardName || '' });
        const res    = await fetch(`/api/board?${params}`);
        const boards = await res.json();

        boardGrid.querySelectorAll('.board-grid-card').forEach(el => el.remove());
        boards.forEach(board => boardGrid.appendChild(MainPage.#createBoardCard(board)));
        boardCountLabel.textContent = `총 ${boards.length}개의 보드`;
    }

    static #initBoardFilter() {
        const btnBoardFilterActive   = document.getElementById('btn-board-filter-active');
        const btnBoardFilterInactive = document.getElementById('btn-board-filter-inactive');
        const boardSearchInput       = document.getElementById('boardSearchInput');

        if (boardSearchInput) {
            boardSearchInput.addEventListener('input', () => {
                clearTimeout(MainPage.#searchTimer);
                MainPage.#searchTimer = setTimeout(() => {
                    MainPage.#loadBoards(MainPage.#currentStatus, boardSearchInput.value.trim());
                }, 300);
            });
        }

        if (btnBoardFilterActive) {
            btnBoardFilterActive.addEventListener('click', () => {
                btnBoardFilterActive.classList.add('active');
                btnBoardFilterInactive.classList.remove('active');
                MainPage.#currentStatus = 'ACTIVE';
                MainPage.#loadBoards(MainPage.#currentStatus, boardSearchInput ? boardSearchInput.value.trim() : '');
            });
        }

        if (btnBoardFilterInactive) {
            btnBoardFilterInactive.addEventListener('click', () => {
                btnBoardFilterInactive.classList.add('active');
                btnBoardFilterActive.classList.remove('active');
                MainPage.#currentStatus = 'INACTIVE';
                MainPage.#loadBoards(MainPage.#currentStatus, boardSearchInput ? boardSearchInput.value.trim() : '');
            });
        }

        MainPage.#loadBoards('ACTIVE', '');
    }

    static #initCreateBoardModal() {
        const createBoardModal   = document.getElementById('createBoardModal');
        const btnOpenBoardCreate = document.getElementById('btn-open-board-create');
        if (!createBoardModal || !btnOpenBoardCreate) { return; }

        btnOpenBoardCreate.addEventListener('click', () => Modal.open('createBoardModal'));

        const closeBoard = () => Modal.close('createBoardModal');
        document.getElementById('btn-close-board-create').addEventListener('click', closeBoard);
        document.getElementById('btn-cancel-board-create').addEventListener('click', closeBoard);

        new CharCounter(document.getElementById('createBoardName'), document.getElementById('createBoardNameCount'), 50, 45);
    }
}

document.addEventListener('DOMContentLoaded', () => MainPage.init());
