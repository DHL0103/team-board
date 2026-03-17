// ── 포스트 목록 제목 검색 ──
(function () {
    var searchInput = document.getElementById('postListSearchInput');
    var countLabel  = document.getElementById('postListCount');
    var cards       = Array.from(document.querySelectorAll('.post-list-card'));

    if (!searchInput) { return; }

    searchInput.addEventListener('input', function () {
        var query = this.value.trim().toLowerCase();
        var count = 0;
        cards.forEach(function (card) {
            var titleEl = card.querySelector('.card-title');
            var matches = !query || (titleEl && titleEl.textContent.toLowerCase().includes(query));
            card.style.display = matches ? '' : 'none';
            if (matches) { count++; }
        });
        if (countLabel) { countLabel.textContent = count + '건'; }
    });
})();
