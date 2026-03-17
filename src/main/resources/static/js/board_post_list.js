(function () {
    var searchInput = document.getElementById('postListSearchInput');
    var countLabel  = document.getElementById('postListCount');
    var listBody    = document.querySelector('.post-list-body');
    var sortBtns    = document.querySelectorAll('.post-list-sort-btn');
    var cards       = Array.from(document.querySelectorAll('.post-list-card'));
    var currentSort = 'newest';

    // ── 정렬 ──
    function sortCards() {
        var sorted = cards.slice().sort(function (a, b) {
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
        sorted.forEach(function (card) { listBody.appendChild(card); });
        cards = sorted;
    }

    // ── 검색 필터 ──
    function applySearch() {
        var query = searchInput ? searchInput.value.trim().toLowerCase() : '';
        var count = 0;
        cards.forEach(function (card) {
            var titleEl = card.querySelector('.card-title');
            var matches = !query || (titleEl && titleEl.textContent.toLowerCase().includes(query));
            card.style.display = matches ? '' : 'none';
            if (matches) { count++; }
        });
        if (countLabel) { countLabel.textContent = count + '건'; }
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
})();
