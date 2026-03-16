(function () {
  var PAGE_SIZE = 10;
  var cards = Array.from(document.querySelectorAll('.search-card'));
  var pagination = document.getElementById('searchPagination');
  var currentPage = 0;

  function showPage(page) {
    currentPage = page;
    cards.forEach(function (card, i) {
      card.style.display = (i >= page * PAGE_SIZE && i < (page + 1) * PAGE_SIZE) ? '' : 'none';
    });
    renderPagination();
  }

  function renderPagination() {
    if (!pagination) { return; }
    var totalPages = Math.ceil(cards.length / PAGE_SIZE);
    if (totalPages <= 1) { return; }

    pagination.innerHTML = '';

    var prev = document.createElement('button');
    prev.className = 'page-btn';
    prev.textContent = '←';
    prev.disabled = currentPage === 0;
    prev.addEventListener('click', function () { showPage(currentPage - 1); });
    pagination.appendChild(prev);

    for (var i = 0; i < totalPages; i++) {
      (function (p) {
        var btn = document.createElement('button');
        btn.className = 'page-btn' + (p === currentPage ? ' active' : '');
        btn.textContent = p + 1;
        btn.addEventListener('click', function () { showPage(p); });
        pagination.appendChild(btn);
      })(i);
    }

    var next = document.createElement('button');
    next.className = 'page-btn';
    next.textContent = '→';
    next.disabled = currentPage === totalPages - 1;
    next.addEventListener('click', function () { showPage(currentPage + 1); });
    pagination.appendChild(next);
  }

  if (cards.length > 0) {
    showPage(0);
  }
})();