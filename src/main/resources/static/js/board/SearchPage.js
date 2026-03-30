class SearchPage {

    static #PAGE_SIZE = 10;
    static #allCards = [];
    static #currentPage = 1;
    static #currentSort = 'recent';

    static init() {
        SearchPage.#allCards = Array.from(document.querySelectorAll('.search-card'));
        SearchPage.#bindInputs();
        SearchPage.#render();
    }

    static #bindInputs() {
        const searchInput  = document.getElementById('search-input');
        const statusSelect = document.getElementById('search-status-select');
        const sortBtns     = document.querySelectorAll('[data-sort]');

        if (searchInput) {
            searchInput.addEventListener('input', () => { SearchPage.#currentPage = 1; SearchPage.#render(); });
        }
        if (statusSelect) {
            statusSelect.addEventListener('change', () => { SearchPage.#currentPage = 1; SearchPage.#render(); });
        }
        sortBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                SearchPage.#currentSort = btn.dataset.sort;
                sortBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                SearchPage.#currentPage = 1;
                SearchPage.#render();
            });
        });
    }

    static #getFiltered() {
        const searchInput  = document.getElementById('search-input');
        const statusSelect = document.getElementById('search-status-select');
        const keyword = searchInput ? searchInput.value.trim().toLowerCase() : '';
        const status  = statusSelect ? statusSelect.value : '';

        return SearchPage.#allCards.filter(card => {
            if (keyword && !card.dataset.name.toLowerCase().includes(keyword)) { return false; }
            if (status && card.dataset.status !== status) { return false; }
            return true;
        });
    }

    static #getSorted(filtered) {
        return filtered.slice().sort((a, b) => {
            if (SearchPage.#currentSort === 'members') {
                return parseInt(b.dataset.memberCount) - parseInt(a.dataset.memberCount);
            }
            return parseInt(b.dataset.id) - parseInt(a.dataset.id);
        });
    }

    static #render() {
        const container  = document.getElementById('search-card-list');
        const pagination = document.getElementById('searchPagination');
        const emptyEl    = document.getElementById('search-empty');

        const filtered   = SearchPage.#getSorted(SearchPage.#getFiltered());
        const totalPages = Math.max(1, Math.ceil(filtered.length / SearchPage.#PAGE_SIZE));
        if (SearchPage.#currentPage > totalPages) { SearchPage.#currentPage = 1; }

        filtered.forEach(c => { container.appendChild(c); c.style.display = 'none'; });
        SearchPage.#allCards.forEach(c => { if (!filtered.includes(c)) { c.style.display = 'none'; } });
        filtered.slice((SearchPage.#currentPage - 1) * SearchPage.#PAGE_SIZE, SearchPage.#currentPage * SearchPage.#PAGE_SIZE)
            .forEach(c => { c.style.display = ''; });

        if (emptyEl) { emptyEl.style.display = filtered.length === 0 ? '' : 'none'; }

        if (pagination) {
            Pagination.render(pagination, totalPages, SearchPage.#currentPage, page => {
                SearchPage.#currentPage = page;
                SearchPage.#render();
            });
        }
    }
}

document.addEventListener('DOMContentLoaded', () => SearchPage.init());
