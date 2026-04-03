class SearchPage {

    static #initialized = false;
    static #PAGE_SIZE = 10;
    static #currentPage = 1;
    static #currentSort = 'recent';
    static #debounceTimer = null;

    static #container;
    static #emptyEl;
    static #pagination;

    static #ROLE_META = {
        MANAGER:   { label: '매니저',  cls: 'role-manager'   },
        USER:      { label: '멤버',    cls: 'role-user'      },
        INVITED:   { label: '초대됨',  cls: 'role-invited'   },
        REQUESTED: { label: '요청 중', cls: 'role-requested' },
    };

    static init() {
        if (SearchPage.#initialized) { return; }
        SearchPage.#initialized = true;

        SearchPage.#container  = document.getElementById('search-card-list');
        SearchPage.#emptyEl    = document.getElementById('search-empty');
        SearchPage.#pagination = document.getElementById('searchPagination');

        SearchPage.#bindInputs();
        SearchPage.#fetchAndRender();
    }

    static #createCard(board) {
        const card = document.createElement('div');
        card.className = 'search-card';

        const left = document.createElement('div');
        left.className = 'search-card-left';

        const dot = document.createElement('span');
        dot.className = `search-board-dot dot-${board.color}`;

        const info = document.createElement('div');
        info.className = 'search-card-info';

        const nameEl = document.createElement('div');
        nameEl.className = 'search-board-name';
        nameEl.innerHTML = '<span></span>';
        nameEl.querySelector('span').textContent = board.name;

        const descEl = document.createElement('div');
        descEl.className = 'search-board-desc';
        descEl.textContent = board.description || '';

        const metaEl = document.createElement('div');
        metaEl.className = 'search-board-meta';
        metaEl.innerHTML =
            '<svg width="11" height="11"><use href="/img/icons.svg#icon-user"></use></svg>' +
            `<span>${board.memberCount}명</span>`;
        if (board.status === 'INACTIVE') {
            metaEl.insertAdjacentHTML('beforeend',
                '<span class="status-badge status-inactive" style="margin-left:4px;">비활성</span>');
        }

        info.appendChild(nameEl);
        info.appendChild(descEl);
        info.appendChild(metaEl);
        left.appendChild(dot);
        left.appendChild(info);

        const right = document.createElement('div');
        right.className = 'search-card-right';

        const roleMeta = SearchPage.#ROLE_META[board.myRole];
        if (roleMeta) {
            const badge = document.createElement('span');
            badge.className = `role-badge ${roleMeta.cls}`;
            badge.textContent = roleMeta.label;
            right.appendChild(badge);
        }

        const goBtn = document.createElement('a');
        goBtn.className = 'btn-board-go';
        goBtn.href = `/boards/${board.id}`;
        goBtn.textContent = '보드로 이동';
        right.appendChild(goBtn);

        card.appendChild(left);
        card.appendChild(right);
        return card;
    }

    static async #fetchAndRender() {
        const searchInput  = document.getElementById('search-input');
        const statusSelect = document.getElementById('search-status-select');

        const params = new URLSearchParams({
            boardName:   searchInput ? searchInput.value.trim() : '',
            boardStatus: statusSelect ? statusSelect.value : '',
            sort:   SearchPage.#currentSort,
            page:   SearchPage.#currentPage - 1,
            size:   SearchPage.#PAGE_SIZE,
        });

        try {
            const res  = await fetch(`/api/boards/search?${params}`);
            const data = await res.json();

            SearchPage.#container.innerHTML = '';

            if (data.boards.length === 0) {
                SearchPage.#emptyEl.style.display = '';
            } else {
                SearchPage.#emptyEl.style.display = 'none';
                data.boards.forEach((board, i) => {
                    const card = SearchPage.#createCard(board);
                    card.style.animationDelay = (i * 40) + 'ms';
                    card.style.animation = 'fadeUp 0.3s ease both';
                    SearchPage.#container.appendChild(card);
                });
            }

            const totalPages = Math.max(1, Math.ceil(data.totalCount / SearchPage.#PAGE_SIZE));
            if (SearchPage.#pagination) {
                Pagination.render(SearchPage.#pagination, totalPages, SearchPage.#currentPage, page => {
                    SearchPage.#currentPage = page;
                    SearchPage.#fetchAndRender();
                    SearchPage.#container.scrollIntoView({ behavior: 'smooth', block: 'start' });
                });
            }
        } catch (e) {
            console.error('보드 목록 로드 실패', e);
        }
    }

    static #bindInputs() {
        const searchInput  = document.getElementById('search-input');
        const statusSelect = document.getElementById('search-status-select');
        const sortBtns     = document.querySelectorAll('[data-sort]');

        if (searchInput) {
            searchInput.addEventListener('input', () => {
                clearTimeout(SearchPage.#debounceTimer);
                SearchPage.#debounceTimer = setTimeout(() => {
                    SearchPage.#currentPage = 1;
                    SearchPage.#fetchAndRender();
                }, 300);
            });
        }

        if (statusSelect) {
            statusSelect.addEventListener('change', () => {
                SearchPage.#currentPage = 1;
                SearchPage.#fetchAndRender();
            });
        }

        sortBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                sortBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                SearchPage.#currentSort = btn.dataset.sort;
                SearchPage.#currentPage = 1;
                SearchPage.#fetchAndRender();
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => SearchPage.init());
