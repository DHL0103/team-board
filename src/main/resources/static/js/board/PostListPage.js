class PostListPage {

    static #initialized = false;
    static #PAGE_SIZE = 10;
    static #boardId;
    static #status;
    static #myPostIds;

    static #currentPage = 1;
    static #currentSort = 'newest';
    static #currentMineOnly = false;
    static #currentQuery = '';
    static #debounceTimer = null;

    static #listBody;
    static #emptyEl;
    static #countLabel;
    static #paginationWrap;

    static #STATUS_META = {
        PROGRESS:  { label: '진행 중',   cls: 'chip-progress'  },
        REJECTED:  { label: '반려',      cls: 'chip-rejected'  },
        REQUESTED: { label: '승인 요청', cls: 'chip-requested' },
        APPROVED:  { label: '완료',      cls: 'chip-completed' },
    };

    static init() {
        if (PostListPage.#initialized) { return; }
        PostListPage.#initialized = true;

        PostListPage.#boardId   = document.body.dataset.boardId;
        PostListPage.#status    = document.body.dataset.status;
        PostListPage.#myPostIds = new Set(JSON.parse(document.body.dataset.myPostIds || '[]'));

        PostListPage.#listBody       = document.getElementById('post-list-body');
        PostListPage.#emptyEl        = document.getElementById('post-list-empty');
        PostListPage.#countLabel     = document.getElementById('postListCount');
        PostListPage.#paginationWrap = document.getElementById('post-list-pagination');

        PostListPage.#bindSortButtons();
        PostListPage.#bindSearch();
        PostListPage.#bindMineFilter();
        PostListPage.#fetchAndRender();
    }

    static #applyDueChip(chipEl, labelEl, dueDate) {
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

    static #createCard(post) {
        const a = document.createElement('a');
        a.className = 'post-list-card';
        a.href = `/board/${PostListPage.#boardId}/post/${post.id}`;

        const top = document.createElement('div');
        top.className = 'card-top';

        const titleEl = document.createElement('div');
        titleEl.className = 'card-title';
        titleEl.textContent = post.title;

        const meta = PostListPage.#STATUS_META[post.status] || { label: post.status, cls: '' };
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
        PostListPage.#applyDueChip(dueChip, dueLabel, dueDate);
        dueChip.appendChild(dueLabel);
        cardLeft.appendChild(dueChip);

        const cardRight = document.createElement('div');
        cardRight.className = 'card-right';

        if (post.hasFiles) {
            cardRight.insertAdjacentHTML('beforeend',
                '<svg class="card-indicator-icon" width="12" height="12"><use href="/img/icons.svg#icon-file"></use></svg>');
        }

        if (PostListPage.#myPostIds.has(post.id)) {
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

    static async #fetchAndRender() {
        const params = new URLSearchParams({
            status:   PostListPage.#status,
            q:        PostListPage.#currentQuery,
            sort:     PostListPage.#currentSort,
            mineOnly: PostListPage.#currentMineOnly,
            page:     PostListPage.#currentPage - 1,
            size:     PostListPage.#PAGE_SIZE,
        });

        try {
            const res  = await fetch(`/api/board/${PostListPage.#boardId}/posts?${params}`);
            const data = await res.json();

            if (PostListPage.#countLabel) { PostListPage.#countLabel.textContent = data.totalCount + '건'; }

            PostListPage.#listBody.innerHTML = '';

            if (data.posts.length === 0) {
                PostListPage.#emptyEl.style.display = '';
            } else {
                PostListPage.#emptyEl.style.display = 'none';
                data.posts.forEach((post, i) => {
                    const card = PostListPage.#createCard(post);
                    card.style.animationDelay = (i * 40) + 'ms';
                    card.style.animation = 'fadeUp 0.3s ease both';
                    PostListPage.#listBody.appendChild(card);
                });
            }

            const totalPages = Math.max(1, Math.ceil(data.totalCount / PostListPage.#PAGE_SIZE));
            if (PostListPage.#paginationWrap) {
                Pagination.render(PostListPage.#paginationWrap, totalPages, PostListPage.#currentPage, page => {
                    PostListPage.#currentPage = page;
                    PostListPage.#fetchAndRender();
                    PostListPage.#listBody.scrollIntoView({ behavior: 'smooth', block: 'start' });
                });
            }
        } catch (e) {
            console.error('게시글 로드 실패', e);
        }
    }

    static #bindSortButtons() {
        const sortBtns = document.querySelectorAll('.post-list-sort-btn');
        sortBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                sortBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                PostListPage.#currentSort = btn.dataset.sort;
                PostListPage.#currentPage = 1;
                PostListPage.#fetchAndRender();
            });
        });
    }

    static #bindSearch() {
        const searchInput = document.getElementById('postListSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', () => {
                clearTimeout(PostListPage.#debounceTimer);
                PostListPage.#debounceTimer = setTimeout(() => {
                    PostListPage.#currentQuery = searchInput.value.trim();
                    PostListPage.#currentPage = 1;
                    PostListPage.#fetchAndRender();
                }, 250);
            });
        }
    }

    static #bindMineFilter() {
        const btnMineFilter = document.getElementById('btn-mine-filter');
        if (btnMineFilter) {
            btnMineFilter.addEventListener('click', () => {
                PostListPage.#currentMineOnly = !PostListPage.#currentMineOnly;
                btnMineFilter.classList.toggle('active', PostListPage.#currentMineOnly);
                PostListPage.#currentPage = 1;
                PostListPage.#fetchAndRender();
            });
        }
    }
}

document.addEventListener('DOMContentLoaded', () => PostListPage.init());
