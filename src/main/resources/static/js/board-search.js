(function () {
  var PAGE_SIZE = 10;
  var allCards = Array.from(document.querySelectorAll('.search-card'));
  var searchInput = document.getElementById('search-input');
  var statusSelect = document.getElementById('search-status-select');
  var sortBtns = document.querySelectorAll('[data-sort]');
  var pagination = document.getElementById('searchPagination');
  var emptyEl = document.getElementById('search-empty');
  var container = document.getElementById('search-card-list');
  var currentPage = 0;
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
    var totalPages = Math.ceil(filtered.length / PAGE_SIZE);
    if (currentPage >= totalPages) { currentPage = 0; }

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
    filtered.slice(currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE).forEach(function (c) {
      c.style.display = '';
    });

    if (emptyEl) { emptyEl.style.display = filtered.length === 0 ? '' : 'none'; }

    renderPagination(totalPages);
  }

  function renderPagination(totalPages) {
    if (!pagination) { return; }
    pagination.innerHTML = '';
    if (totalPages <= 1) { return; }

    var prev = document.createElement('button');
    prev.className = 'page-btn';
    prev.textContent = '←';
    prev.disabled = currentPage === 0;
    prev.addEventListener('click', function () { currentPage--; render(); });
    pagination.appendChild(prev);

    for (var i = 0; i < totalPages; i++) {
      (function (p) {
        var btn = document.createElement('button');
        btn.className = 'page-btn' + (p === currentPage ? ' active' : '');
        btn.textContent = p + 1;
        btn.addEventListener('click', function () { currentPage = p; render(); });
        pagination.appendChild(btn);
      })(i);
    }

    var next = document.createElement('button');
    next.className = 'page-btn';
    next.textContent = '→';
    next.disabled = currentPage === totalPages - 1;
    next.addEventListener('click', function () { currentPage++; render(); });
    pagination.appendChild(next);
  }

  if (searchInput) {
    searchInput.addEventListener('input', function () { currentPage = 0; render(); });
  }

  if (statusSelect) {
    statusSelect.addEventListener('change', function () { currentPage = 0; render(); });
  }

  sortBtns.forEach(function (btn) {
    btn.addEventListener('click', function () {
      currentSort = btn.dataset.sort;
      sortBtns.forEach(function (b) { b.classList.remove('active'); });
      btn.classList.add('active');
      currentPage = 0;
      render();
    });
  });

  render();
})();