class MainPage {

    static init() {
        Modal.init();
        MainPage.#initKanban();
        MainPage.#initBoardFilter();
        MainPage.#initCreateBoardModal();
    }

    static #initKanban() {
        const colMap = {};
        document.querySelectorAll('.kanban-col[data-board-id]').forEach(col => {
            const id = col.dataset.boardId;
            colMap[id] = { bodyId: `body-${id}`, countId: `count-${id}`, emptyId: `empty-${id}` };
        });

        Object.values(colMap).forEach(({ bodyId, countId, emptyId }) => {
            const body = document.getElementById(bodyId);
            if (!body) { return; }
            const cards = body.querySelectorAll('.board-card');
            let visible = 0;
            cards.forEach(card => {
                if (card.dataset.status === 'COMPLETED') { card.style.display = 'none'; } else { visible++; }
            });
            document.getElementById(countId).textContent = visible + '개';
            if (emptyId && visible === 0) { document.getElementById(emptyId).style.display = 'flex'; }
        });

        document.querySelectorAll('.status-chip').forEach(chip => {
            const s = chip.dataset.status;
            if (s === 'PROGRESS')  { chip.classList.add('progress'); }
            if (s === 'REQUESTED') { chip.classList.add('requested'); }
            if (s === 'REJECTED')  { chip.classList.add('rejected'); }
        });

        document.querySelectorAll('.board-card').forEach(card => {
            card.addEventListener('mouseenter', () => card.classList.add('is-hovered'));
            card.addEventListener('mouseleave', () => card.classList.remove('is-hovered'));
            card.addEventListener('click', () => {
                const postId = card.dataset.id;
                if (postId) { location.href = `/post/${postId}`; }
            });
        });

        document.querySelectorAll('.view-tab').forEach(tab => {
            tab.addEventListener('click', () => {
                document.querySelectorAll('.view-tab').forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
            });
        });
    }

    static #initBoardFilter() {
        const allBoardCards          = Array.from(document.querySelectorAll('.board-grid-card[data-status]'));
        const boardCountLabel        = document.getElementById('board-count-label');
        const btnBoardFilterActive   = document.getElementById('btn-board-filter-active');
        const btnBoardFilterInactive = document.getElementById('btn-board-filter-inactive');
        const boardSearchInput       = document.getElementById('boardSearchInput');
        let currentFilterStatus      = 'ACTIVE';

        function applyFilters() {
            const query = boardSearchInput ? boardSearchInput.value.trim().toLowerCase() : '';
            let count = 0;
            allBoardCards.forEach(card => {
                const nameEl = card.querySelector('.board-grid-card-name');
                const visible = card.dataset.status === currentFilterStatus
                    && (!query || (nameEl && nameEl.textContent.toLowerCase().includes(query)));
                card.style.display = visible ? '' : 'none';
                if (visible) { count++; }
            });
            boardCountLabel.textContent = `총 ${count}개의 보드`;
        }

        if (boardSearchInput) { boardSearchInput.addEventListener('input', applyFilters); }

        if (btnBoardFilterActive) {
            btnBoardFilterActive.addEventListener('click', () => {
                btnBoardFilterActive.classList.add('active');
                btnBoardFilterInactive.classList.remove('active');
                currentFilterStatus = 'ACTIVE';
                applyFilters();
            });
        }
        if (btnBoardFilterInactive) {
            btnBoardFilterInactive.addEventListener('click', () => {
                btnBoardFilterInactive.classList.add('active');
                btnBoardFilterActive.classList.remove('active');
                currentFilterStatus = 'INACTIVE';
                applyFilters();
            });
        }

        applyFilters();
    }

    static #initCreateBoardModal() {
        const createBoardModal  = document.getElementById('createBoardModal');
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
