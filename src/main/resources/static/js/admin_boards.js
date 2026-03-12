const PAGE_SIZE = 10;

const tbody = document.getElementById("board-tbody");
const searchInput = document.getElementById("board-search");
const paginationWrap = document.getElementById("pagination-wrap");

if (tbody) {
    const allRows = Array.from(tbody.querySelectorAll("tr"));
    let filteredRows = allRows;
    let currentPage = 1;

    function render() {
        const start = (currentPage - 1) * PAGE_SIZE;
        const end = start + PAGE_SIZE;
        allRows.forEach(row => { row.style.display = "none"; });
        filteredRows.slice(start, end).forEach(row => { row.style.display = ""; });
        renderPagination();
    }

    function renderPagination() {
        const totalPages = Math.max(1, Math.ceil(filteredRows.length / PAGE_SIZE));
        paginationWrap.innerHTML = "";

        if (totalPages <= 1) {
            return;
        }

        const prev = document.createElement("button");
        prev.className = "page-btn";
        prev.textContent = "이전";
        prev.disabled = currentPage === 1;
        prev.addEventListener("click", () => { currentPage--; render(); });
        paginationWrap.appendChild(prev);

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.className = "page-btn" + (i === currentPage ? " active" : "");
            btn.textContent = i;
            btn.addEventListener("click", () => { currentPage = i; render(); });
            paginationWrap.appendChild(btn);
        }

        const next = document.createElement("button");
        next.className = "page-btn";
        next.textContent = "다음";
        next.disabled = currentPage === totalPages;
        next.addEventListener("click", () => { currentPage++; render(); });
        paginationWrap.appendChild(next);
    }

    searchInput.addEventListener("input", () => {
        const query = searchInput.value.trim().toLowerCase();
        filteredRows = allRows.filter(row => row.dataset.name.toLowerCase().includes(query));
        currentPage = 1;
        render();
    });

    render();
}
