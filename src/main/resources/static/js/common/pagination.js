// ── 페이지네이션 ──
App.renderPagination = function (paginationEl, totalPages, currentPage, onPageChange) {
    paginationEl.innerHTML = '';
    if (totalPages <= 1) { return; }

    const prev = document.createElement('button');
    prev.className = 'page-btn';
    prev.textContent = '이전';
    prev.disabled = currentPage === 1;
    prev.addEventListener('click', function () { onPageChange(currentPage - 1); });
    paginationEl.appendChild(prev);

    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement('button');
        btn.className = 'page-btn' + (i === currentPage ? ' active' : '');
        btn.textContent = i;
        btn.addEventListener('click', (function (page) {
            return function () { onPageChange(page); };
        })(i));
        paginationEl.appendChild(btn);
    }

    const next = document.createElement('button');
    next.className = 'page-btn';
    next.textContent = '다음';
    next.disabled = currentPage === totalPages;
    next.addEventListener('click', function () { onPageChange(currentPage + 1); });
    paginationEl.appendChild(next);
};

App.makePaginator = function (items, paginationEl, pageSize) {
    let currentPage = 1;

    function render() {
        const start = (currentPage - 1) * pageSize;
        const end   = start + pageSize;
        items.forEach(function (item, i) {
            item.style.display = (i >= start && i < end) ? '' : 'none';
        });
        const totalPages = Math.max(1, Math.ceil(items.length / pageSize));
        App.renderPagination(paginationEl, totalPages, currentPage, function (page) {
            currentPage = page;
            render();
        });
    }

    render();
};
