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
        var start    = (currentPage - 1) * PAGE_SIZE;
        var end      = start + PAGE_SIZE;
        var toShow   = filteredCards.slice(start, end);
        var stagger  = 45;
        var duration = '0.3s';

        cards.forEach(function (card) {
            card.style.animation = '';
            card.style.animationDelay = '';
            card.style.display = 'none';
        });

        if (countLabel) { countLabel.textContent = filteredCards.length + '건'; }
        var totalPages = Math.max(1, Math.ceil(filteredCards.length / PAGE_SIZE));
        createPagination(paginationWrap, currentPage, totalPages, function (p) { currentPage = p; render(); });

        // display:none이 브라우저에 반영된 다음 프레임에서 애니메이션 시작
        requestAnimationFrame(function () {
            toShow.forEach(function (card, i) {
                card.style.animationDelay = (i * stagger) + 'ms';
                card.style.animation = 'fadeUp ' + duration + ' ease both';
                card.style.display = '';
            });
        });
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
