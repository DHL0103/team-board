(function () {
    const PAGE_SIZE = 10;

    const searchInput  = document.getElementById('search-input');
    const statusSelect = document.getElementById('search-status-select');
    const sortBtns     = document.querySelectorAll('[data-sort]');
    const pagination   = document.getElementById('searchPagination');
    const emptyEl      = document.getElementById('search-empty');
    const container    = document.getElementById('search-card-list');

    let currentPage   = 1;
    let currentSort   = 'recent';
    let debounceTimer = null;

    const ROLE_META = {
        MANAGER:   { label: '매니저',  cls: 'role-manager'   },
        USER:      { label: '멤버',    cls: 'role-user'      },
        INVITED:   { label: '초대됨',  cls: 'role-invited'   },
        REQUESTED: { label: '요청 중', cls: 'role-requested' },
    };

    function createCard(board) {
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
        nameEl.innerHTML = `<span></span>`;
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

        const roleMeta = ROLE_META[board.myRole];
        if (roleMeta) {
            const badge = document.createElement('span');
            badge.className = `role-badge ${roleMeta.cls}`;
            badge.textContent = roleMeta.label;
            right.appendChild(badge);
        }

        const goBtn = document.createElement('a');
        goBtn.className = 'btn-board-go';
        goBtn.href = `/board/${board.id}`;
        goBtn.textContent = '보드로 이동';
        right.appendChild(goBtn);

        card.appendChild(left);
        card.appendChild(right);
        return card;
    }

    async function fetchAndRender() {
        const params = new URLSearchParams({
            q:      searchInput ? searchInput.value.trim() : '',
            status: statusSelect ? statusSelect.value : '',
            sort:   currentSort,
            page:   currentPage - 1,
            size:   PAGE_SIZE,
        });

        try {
            const res  = await fetch(`/api/boards/search?${params}`);
            const data = await res.json();

            container.innerHTML = '';

            if (data.boards.length === 0) {
                emptyEl.style.display = '';
            } else {
                emptyEl.style.display = 'none';
                data.boards.forEach((board, i) => {
                    const card = createCard(board);
                    card.style.animationDelay = (i * 40) + 'ms';
                    card.style.animation = 'fadeUp 0.3s ease both';
                    container.appendChild(card);
                });
            }

            const totalPages = Math.max(1, Math.ceil(data.totalCount / PAGE_SIZE));
            if (pagination) {
                App.renderPagination(pagination, totalPages, currentPage, page => {
                    currentPage = page;
                    fetchAndRender();
                    container.scrollIntoView({ behavior: 'smooth', block: 'start' });
                });
            }
        } catch (e) {
            console.error('보드 목록 로드 실패', e);
        }
    }

    if (searchInput) {
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                currentPage = 1;
                fetchAndRender();
            }, 250);
        });
    }

    if (statusSelect) {
        statusSelect.addEventListener('change', () => {
            currentPage = 1;
            fetchAndRender();
        });
    }

    sortBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            sortBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentSort = btn.dataset.sort;
            currentPage = 1;
            fetchAndRender();
        });
    });

    fetchAndRender();
})();
