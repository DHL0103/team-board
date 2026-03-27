(function () {
    const boardId   = document.body.dataset.boardId;
    const status    = document.body.dataset.status;
    const myPostIds = new Set(JSON.parse(document.body.dataset.myPostIds || '[]'));

    const PAGE_SIZE = 10;

    const searchInput    = document.getElementById('postListSearchInput');
    const countLabel     = document.getElementById('postListCount');
    const listBody       = document.getElementById('post-list-body');
    const emptyEl        = document.getElementById('post-list-empty');
    const paginationWrap = document.getElementById('post-list-pagination');
    const sortBtns       = document.querySelectorAll('.post-list-sort-btn');
    const btnMineFilter  = document.getElementById('btn-mine-filter');

    let currentPage     = 1;
    let currentSort     = 'newest';
    let currentMineOnly = false;
    let currentQuery    = '';
    let debounceTimer   = null;

    const STATUS_META = {
        PROGRESS:  { label: '진행 중',   cls: 'chip-progress'  },
        REJECTED:  { label: '반려',      cls: 'chip-rejected'  },
        REQUESTED: { label: '승인 요청', cls: 'chip-requested' },
        APPROVED:  { label: '완료',      cls: 'chip-completed' },
    };

    function applyDueChip(chipEl, labelEl, dueDate) {
        if (!dueDate) { chipEl.classList.add('none'); return; }
        const today = new Date(); today.setHours(0, 0, 0, 0);
        const due   = new Date(dueDate + 'T00:00:00');
        const diffDays = Math.floor((due - today) / 86400000);
        if (diffDays < 0)        { chipEl.classList.add('over'); labelEl.textContent += ' 초과'; }
        else if (diffDays === 0) { chipEl.classList.add('warn'); labelEl.textContent = '오늘 마감'; }
        else if (diffDays === 1) { chipEl.classList.add('warn'); labelEl.textContent = '내일 마감'; }
        else if (diffDays <= 3)  { chipEl.classList.add('warn'); labelEl.textContent += ' 마감'; }
        else                     { chipEl.classList.add('safe'); labelEl.textContent += ' 마감'; }
    }

    function createCard(post) {
        const a = document.createElement('a');
        a.className = 'post-list-card';
        a.href = `/board/${boardId}/post/${post.id}`;

        const top = document.createElement('div');
        top.className = 'card-top';

        const titleEl = document.createElement('div');
        titleEl.className = 'card-title';
        titleEl.textContent = post.title;

        const meta = STATUS_META[post.status] || { label: post.status, cls: '' };
        const chipEl = document.createElement('span');
        chipEl.className = `status-chip ${meta.cls}`;
        chipEl.textContent = meta.label;

        top.appendChild(titleEl);
        top.appendChild(chipEl);

        const contentEl = document.createElement('div');
        contentEl.className = 'card-content';
        contentEl.textContent = post.plainContent || '';

        const footer = document.createElement('div');
        footer.className = 'card-footer';

        const cardLeft = document.createElement('div');
        cardLeft.className = 'card-left';

        const dueDate = post.dueDate ? post.dueDate.substring(0, 10) : null;
        const dueChip = document.createElement('span');
        dueChip.className = 'due-chip';
        dueChip.innerHTML = '<svg width="10" height="10"><use href="/img/icons.svg#icon-calendar"></use></svg>';

        const dueLabel = document.createElement('span');
        dueLabel.className = 'due-label';
        dueLabel.textContent = dueDate ? dueDate.substring(5).replace('-', '.') : '기한 없음';
        applyDueChip(dueChip, dueLabel, dueDate);
        dueChip.appendChild(dueLabel);
        cardLeft.appendChild(dueChip);

        const cardRight = document.createElement('div');
        cardRight.className = 'card-right';

        if (myPostIds.has(post.id)) {
            cardRight.insertAdjacentHTML('beforeend',
                '<svg class="card-mine-icon" width="12" height="12"><use href="/img/icons.svg#icon-user"></use></svg>');
        }

        const dateEl = document.createElement('span');
        dateEl.className = 'card-date';
        dateEl.textContent = post.createdAt ? post.createdAt.substring(5, 10).replace('-', '.') : '';
        cardRight.appendChild(dateEl);

        footer.appendChild(cardLeft);
        footer.appendChild(cardRight);
        a.appendChild(top);
        a.appendChild(contentEl);
        a.appendChild(footer);

        return a;
    }

    async function fetchAndRender() {
        const params = new URLSearchParams({
            status,
            q:        currentQuery,
            sort:     currentSort,
            mineOnly: currentMineOnly,
            page:     currentPage - 1,
            size:     PAGE_SIZE,
        });

        try {
            const res  = await fetch(`/api/board/${boardId}/posts?${params}`);
            const data = await res.json();

            if (countLabel) { countLabel.textContent = data.totalCount + '건'; }

            listBody.innerHTML = '';

            if (data.posts.length === 0) {
                emptyEl.style.display = '';
            } else {
                emptyEl.style.display = 'none';
                data.posts.forEach((post, i) => {
                    const card = createCard(post);
                    card.style.animationDelay = (i * 40) + 'ms';
                    card.style.animation = 'fadeUp 0.3s ease both';
                    listBody.appendChild(card);
                });
            }

            const totalPages = Math.max(1, Math.ceil(data.totalCount / PAGE_SIZE));
            if (paginationWrap) {
                App.renderPagination(paginationWrap, totalPages, currentPage, page => {
                    currentPage = page;
                    fetchAndRender();
                    listBody.scrollIntoView({ behavior: 'smooth', block: 'start' });
                });
            }
        } catch (e) {
            console.error('게시글 로드 실패', e);
        }
    }

    // ── 정렬 버튼 ──
    sortBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            sortBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentSort = btn.dataset.sort;
            currentPage = 1;
            fetchAndRender();
        });
    });

    // ── 검색 입력 ──
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                currentQuery = searchInput.value.trim();
                currentPage  = 1;
                fetchAndRender();
            }, 250);
        });
    }

    // ── 내 담당 필터 ──
    if (btnMineFilter) {
        btnMineFilter.addEventListener('click', () => {
            currentMineOnly = !currentMineOnly;
            btnMineFilter.classList.toggle('active', currentMineOnly);
            currentPage = 1;
            fetchAndRender();
        });
    }

    fetchAndRender();
})();
