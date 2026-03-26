(function () {
    const PAGE_SIZE = 10;

    const searchInput    = document.getElementById('postListSearchInput');
    const countLabel     = document.getElementById('postListCount');
    const listBody       = document.querySelector('.post-list-body');
    const paginationWrap = document.getElementById('post-list-pagination');
    const sortBtns       = document.querySelectorAll('.post-list-sort-btn');
    const btnMineFilter  = document.getElementById('btn-mine-filter');
    let cards            = Array.from(document.querySelectorAll('.post-list-card'));
    let currentSort      = 'newest';
    let currentPage      = 1;
    let currentMineOnly  = false;
    let filteredCards    = [];

    // ── 정렬 ──
    function sortCards() {
        cards = cards.slice().sort((a, b) => {
            if (currentSort === 'newest') {
                return b.dataset.createdAt > a.dataset.createdAt ? 1 : -1;
            }
            if (currentSort === 'oldest') {
                return a.dataset.createdAt > b.dataset.createdAt ? 1 : -1;
            }
            if (currentSort === 'due') {
                const da = a.dataset.due, db = b.dataset.due;
                if (!da && !db) { return 0; }
                if (!da) { return 1; }
                if (!db) { return -1; }
                return da > db ? 1 : -1;
            }
            return 0;
        });
        cards.forEach(card => listBody.appendChild(card));
    }

    // ── 페이지 렌더 ──
    function render() {
        const start    = (currentPage - 1) * PAGE_SIZE;
        const end      = start + PAGE_SIZE;
        const toShow   = filteredCards.slice(start, end);
        const stagger  = 45;
        const duration = '0.3s';

        cards.forEach(card => {
            card.style.animation = '';
            card.style.animationDelay = '';
            card.style.display = 'none';
        });

        if (countLabel) { countLabel.textContent = filteredCards.length + '건'; }

        if (paginationWrap) {
            const totalPages = Math.max(1, Math.ceil(filteredCards.length / PAGE_SIZE));
            renderPagination(paginationWrap, totalPages, currentPage, page => {
                currentPage = page;
                render();
            });
        }

        // display:none이 브라우저에 반영된 다음 프레임에서 애니메이션 시작
        requestAnimationFrame(() => {
            toShow.forEach((card, i) => {
                card.style.animationDelay = (i * stagger) + 'ms';
                card.style.animation = 'fadeUp ' + duration + ' ease both';
                card.style.display = '';
            });
        });
    }

    // ── 검색 + 내 담당 필터 ──
    function applySearch() {
        const query = searchInput ? searchInput.value.trim().toLowerCase() : '';
        filteredCards = cards.filter(card => {
            const titleEl = card.querySelector('.card-title');
            const matchesSearch = !query || (titleEl && titleEl.textContent.toLowerCase().includes(query));
            const matchesMine = !currentMineOnly || card.dataset.isMine === 'true';
            return matchesSearch && matchesMine;
        });
        currentPage = 1;
        render();
    }

    // ── 정렬 버튼 ──
    sortBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            sortBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentSort = btn.dataset.sort;
            sortCards();
            applySearch();
        });
    });

    // ── 검색 입력 ──
    if (searchInput) {
        searchInput.addEventListener('input', applySearch);
    }

    // ── 내 담당 필터 ──
    if (btnMineFilter) {
        btnMineFilter.addEventListener('click', () => {
            currentMineOnly = !currentMineOnly;
            btnMineFilter.classList.toggle('active', currentMineOnly);
            applySearch();
        });
    }

    applySearch();
})();
