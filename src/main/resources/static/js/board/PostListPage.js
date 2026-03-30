class PostListPage {

    static #PAGE_SIZE = 10;
    static #cards = [];
    static #filteredCards = [];
    static #currentSort = 'newest';
    static #currentPage = 1;
    static #currentMineOnly = false;

    static init() {
        DueChip.init();
        PostListPage.#cards = Array.from(document.querySelectorAll('.post-list-card'));
        PostListPage.#bindSortButtons();
        PostListPage.#bindSearch();
        PostListPage.#bindMineFilter();
        PostListPage.#applySearch();
    }

    static #bindSortButtons() {
        const sortBtns = document.querySelectorAll('.post-list-sort-btn');
        sortBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                sortBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                PostListPage.#currentSort = btn.dataset.sort;
                PostListPage.#sortCards();
                PostListPage.#applySearch();
            });
        });
    }

    static #bindSearch() {
        const searchInput = document.getElementById('postListSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', () => PostListPage.#applySearch());
        }
    }

    static #bindMineFilter() {
        const btnMineFilter = document.getElementById('btn-mine-filter');
        if (btnMineFilter) {
            btnMineFilter.addEventListener('click', () => {
                PostListPage.#currentMineOnly = !PostListPage.#currentMineOnly;
                btnMineFilter.classList.toggle('active', PostListPage.#currentMineOnly);
                PostListPage.#applySearch();
            });
        }
    }

    static #sortCards() {
        const listBody = document.querySelector('.post-list-body');
        PostListPage.#cards = PostListPage.#cards.slice().sort((a, b) => {
            if (PostListPage.#currentSort === 'newest') { return b.dataset.createdAt > a.dataset.createdAt ? 1 : -1; }
            if (PostListPage.#currentSort === 'oldest') { return a.dataset.createdAt > b.dataset.createdAt ? 1 : -1; }
            if (PostListPage.#currentSort === 'due') {
                const da = a.dataset.due, db = b.dataset.due;
                if (!da && !db) { return 0; }
                if (!da) { return 1; }
                if (!db) { return -1; }
                return da > db ? 1 : -1;
            }
            return 0;
        });
        PostListPage.#cards.forEach(card => listBody.appendChild(card));
    }

    static #applySearch() {
        const searchInput  = document.getElementById('postListSearchInput');
        const countLabel   = document.getElementById('postListCount');
        const paginationWrap = document.getElementById('post-list-pagination');
        const query = searchInput ? searchInput.value.trim().toLowerCase() : '';

        PostListPage.#filteredCards = PostListPage.#cards.filter(card => {
            const titleEl = card.querySelector('.card-title');
            const matchesSearch = !query || (titleEl && titleEl.textContent.toLowerCase().includes(query));
            const matchesMine   = !PostListPage.#currentMineOnly || card.dataset.isMine === 'true';
            return matchesSearch && matchesMine;
        });
        PostListPage.#currentPage = 1;
        PostListPage.#render(countLabel, paginationWrap);
    }

    static #render(countLabel, paginationWrap) {
        const start    = (PostListPage.#currentPage - 1) * PostListPage.#PAGE_SIZE;
        const end      = start + PostListPage.#PAGE_SIZE;
        const toShow   = PostListPage.#filteredCards.slice(start, end);
        const stagger  = 45;
        const duration = '0.3s';

        PostListPage.#cards.forEach(card => {
            card.style.animation      = '';
            card.style.animationDelay = '';
            card.style.display        = 'none';
        });

        if (countLabel) { countLabel.textContent = PostListPage.#filteredCards.length + '건'; }

        if (paginationWrap) {
            const totalPages = Math.max(1, Math.ceil(PostListPage.#filteredCards.length / PostListPage.#PAGE_SIZE));
            Pagination.render(paginationWrap, totalPages, PostListPage.#currentPage, page => {
                PostListPage.#currentPage = page;
                PostListPage.#render(countLabel, paginationWrap);
            });
        }

        requestAnimationFrame(() => {
            toShow.forEach((card, i) => {
                card.style.animationDelay = (i * stagger) + 'ms';
                card.style.animation      = 'fadeUp ' + duration + ' ease both';
                card.style.display        = '';
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => PostListPage.init());
