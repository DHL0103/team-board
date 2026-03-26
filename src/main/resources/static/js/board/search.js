(function () {
  const PAGE_SIZE = 10;
  const allCards = Array.from(document.querySelectorAll('.search-card'));
  const searchInput = document.getElementById('search-input');
  const statusSelect = document.getElementById('search-status-select');
  const sortBtns = document.querySelectorAll('[data-sort]');
  const pagination = document.getElementById('searchPagination');
  const emptyEl = document.getElementById('search-empty');
  const container = document.getElementById('search-card-list');
  let currentPage = 1;
  let currentSort = 'recent';

  function getFiltered() {
    const keyword = searchInput ? searchInput.value.trim().toLowerCase() : '';
    const status = statusSelect ? statusSelect.value : '';

    return allCards.filter(card => {
      if (keyword && !card.dataset.name.toLowerCase().includes(keyword)) { return false; }
      if (status && card.dataset.status !== status) { return false; }
      return true;
    });
  }

  function getSorted(filtered) {
    return filtered.slice().sort((a, b) => {
      if (currentSort === 'members') {
        return parseInt(b.dataset.memberCount) - parseInt(a.dataset.memberCount);
      }
      return parseInt(b.dataset.id) - parseInt(a.dataset.id);
    });
  }

  function render() {
    const filtered = getSorted(getFiltered());
    const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
    if (currentPage > totalPages) { currentPage = 1; }

    // DOM 순서 재배치 후 전부 숨김
    filtered.forEach(c => {
      container.appendChild(c);
      c.style.display = 'none';
    });

    // 필터 제외 카드 숨김
    allCards.forEach(c => {
      if (!filtered.includes(c)) { c.style.display = 'none'; }
    });

    // 현재 페이지 카드만 표시
    filtered.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE).forEach(c => {
      c.style.display = '';
    });

    if (emptyEl) { emptyEl.style.display = filtered.length === 0 ? '' : 'none'; }

    if (pagination) {
      App.renderPagination(pagination, totalPages, currentPage, page => {
        currentPage = page;
        render();
      });
    }
  }

  if (searchInput) {
    searchInput.addEventListener('input', () => { currentPage = 1; render(); });
  }

  if (statusSelect) {
    statusSelect.addEventListener('change', () => { currentPage = 1; render(); });
  }

  sortBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      currentSort = btn.dataset.sort;
      sortBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      currentPage = 1;
      render();
    });
  });

  render();
})();
