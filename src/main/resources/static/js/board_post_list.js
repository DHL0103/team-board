(function () {
    var PAGE_SIZE = 10;

    var searchInput    = document.getElementById('postListSearchInput');
    var countLabel     = document.getElementById('postListCount');
    var listBody       = document.querySelector('.post-list-body');
    var paginationWrap = document.getElementById('post-list-pagination');
    var sortBtns       = document.querySelectorAll('.post-list-sort-btn');
    var cards          = Array.from(document.querySelectorAll('.post-list-card'));
    var currentSort    = 'newest';
    var currentPage    = 1;
    var filteredCards  = [];

    // ── 정렬 ──
    function sortCards() {
        cards = cards.slice().sort(function (a, b) {
            if (currentSort === 'newest') {
                return b.dataset.createdAt > a.dataset.createdAt ? 1 : -1;
            }
            if (currentSort === 'oldest') {
                return a.dataset.createdAt > b.dataset.createdAt ? 1 : -1;
            }
            if (currentSort === 'due') {
                var da = a.dataset.due, db = b.dataset.due;
                if (!da && !db) { return 0; }
                if (!da) { return 1; }
                if (!db) { return -1; }
                return da > db ? 1 : -1;
            }
            return 0;
        });
        cards.forEach(function (card) { listBody.appendChild(card); });
    }

    // ── 페이지 렌더 ──
    function render() {
        var start = (currentPage - 1) * PAGE_SIZE;
        var end   = start + PAGE_SIZE;
        cards.forEach(function (card) { card.style.display = 'none'; });
        filteredCards.slice(start, end).forEach(function (card) { card.style.display = ''; });
        if (countLabel) { countLabel.textContent = filteredCards.length + '건'; }
        renderPagination();
    }

    function renderPagination() {
        if (!paginationWrap) { return; }
        var totalPages = Math.max(1, Math.ceil(filteredCards.length / PAGE_SIZE));
        paginationWrap.innerHTML = '';

        if (totalPages <= 1) { return; }

        var prev = document.createElement('button');
        prev.className = 'page-btn';
        prev.textContent = '이전';
        prev.disabled = currentPage === 1;
        prev.addEventListener('click', function () { currentPage--; render(); });
        paginationWrap.appendChild(prev);

        for (var i = 1; i <= totalPages; i++) {
            (function (page) {
                var btn = document.createElement('button');
                btn.className = 'page-btn' + (page === currentPage ? ' active' : '');
                btn.textContent = page;
                btn.addEventListener('click', function () { currentPage = page; render(); });
                paginationWrap.appendChild(btn);
            })(i);
        }

        var next = document.createElement('button');
        next.className = 'page-btn';
        next.textContent = '다음';
        next.disabled = currentPage === totalPages;
        next.addEventListener('click', function () { currentPage++; render(); });
        paginationWrap.appendChild(next);
    }

    // ── 검색 필터 ──
    function applySearch() {
        var query = searchInput ? searchInput.value.trim().toLowerCase() : '';
        filteredCards = cards.filter(function (card) {
            var titleEl = card.querySelector('.card-title');
            return !query || (titleEl && titleEl.textContent.toLowerCase().includes(query));
        });
        currentPage = 1;
        render();
    }

    // ── 정렬 버튼 ──
    sortBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            sortBtns.forEach(function (b) { b.classList.remove('active'); });
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

    applySearch();
})();
