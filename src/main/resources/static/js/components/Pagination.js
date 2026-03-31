class Pagination {

    #items;
    #paginationEl;
    #pageSize;
    #currentPage = 1;

    // 서버사이드 페이지네이션 버튼 렌더링
    static render(paginationEl, totalPages, currentPage, onPageChange) {
        paginationEl.innerHTML = '';
        if (totalPages <= 1) { return; }

        const prev = document.createElement('button');
        prev.className = 'page-btn';
        prev.textContent = '이전';
        prev.disabled = currentPage === 1;
        prev.addEventListener('click', () => onPageChange(currentPage - 1));
        paginationEl.appendChild(prev);

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement('button');
            btn.className = 'page-btn' + (i === currentPage ? ' active' : '');
            btn.textContent = i;
            btn.addEventListener('click', (page => () => onPageChange(page))(i));
            paginationEl.appendChild(btn);
        }

        const next = document.createElement('button');
        next.className = 'page-btn';
        next.textContent = '다음';
        next.disabled = currentPage === totalPages;
        next.addEventListener('click', () => onPageChange(currentPage + 1));
        paginationEl.appendChild(next);
    }

    // 클라이언트사이드 페이지네이션 (DOM 아이템 직접 제어)
    constructor(items, paginationEl, pageSize) {
        this.#items        = items;
        this.#paginationEl = paginationEl;
        this.#pageSize     = pageSize;
        this.#render();
    }

    #render() {
        const start = (this.#currentPage - 1) * this.#pageSize;
        const end   = start + this.#pageSize;
        this.#items.forEach((item, i) => {
            item.style.display = (i >= start && i < end) ? '' : 'none';
        });
        const totalPages = Math.max(1, Math.ceil(this.#items.length / this.#pageSize));
        Pagination.render(this.#paginationEl, totalPages, this.#currentPage, page => {
            this.#currentPage = page;
            this.#render();
        });
    }
}
