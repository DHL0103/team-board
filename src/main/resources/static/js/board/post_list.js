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
                    const card = App.createPostCard(post, { boardId, myPostIds, asLink: true });
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
