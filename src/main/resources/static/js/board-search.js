(function () {
  var PAGE_SIZE = 10;
  var allCards = Array.from(document.querySelectorAll('.search-card'));
  var searchInput = document.getElementById('search-input');
  var statusSelect = document.getElementById('search-status-select');
  var sortBtns = document.querySelectorAll('[data-sort]');
  var pagination = document.getElementById('searchPagination');
  var emptyEl = document.getElementById('search-empty');
  var container = document.getElementById('search-card-list');
  var currentPage = 1;
  var currentSort = 'recent';

  function getFiltered() {
    var keyword = searchInput ? searchInput.value.trim().toLowerCase() : '';
    var status = statusSelect ? statusSelect.value : '';

    return allCards.filter(function (card) {
      if (keyword && !card.dataset.name.toLowerCase().includes(keyword)) { return false; }
      if (status && card.dataset.status !== status) { return false; }
      return true;
    });
  }

  function getSorted(filtered) {
    return filtered.slice().sort(function (a, b) {
      if (currentSort === 'members') {
        return parseInt(b.dataset.memberCount) - parseInt(a.dataset.memberCount);
      }
      return parseInt(b.dataset.id) - parseInt(a.dataset.id);
    });
  }

  function render() {
    var filtered = getSorted(getFiltered());
    var totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
    if (currentPage > totalPages) { currentPage = 1; }

    // DOM 순서 재배치 후 전부 숨김
    filtered.forEach(function (c) {
      container.appendChild(c);
      c.style.display = 'none';
    });

    // 필터 제외 카드 숨김
    allCards.forEach(function (c) {
      if (!filtered.includes(c)) { c.style.display = 'none'; }
    });

    // 현재 페이지 카드만 표시
    filtered.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE).forEach(function (c) {
      c.style.display = '';
    });

    if (emptyEl) { emptyEl.style.display = filtered.length === 0 ? '' : 'none'; }

    createPagination(pagination, currentPage, totalPages, function (p) { currentPage = p; render(); });
  }

  if (searchInput) {
    searchInput.addEventListener('input', function () { currentPage = 1; render(); });
  }

  if (statusSelect) {
    statusSelect.addEventListener('change', function () { currentPage = 1; render(); });
  }

  sortBtns.forEach(function (btn) {
    btn.addEventListener('click', function () {
      currentSort = btn.dataset.sort;
      sortBtns.forEach(function (b) { b.classList.remove('active'); });
      btn.classList.add('active');
      currentPage = 1;
      render();
    });
  });

  render();
})();