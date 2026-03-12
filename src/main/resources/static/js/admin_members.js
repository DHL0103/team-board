const PAGE_SIZE = 10;

const tbody = document.getElementById("member-tbody");
const searchInput = document.getElementById("member-search");
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
        filteredRows = allRows.filter(row => row.dataset.username.toLowerCase().includes(query));
        currentPage = 1;
        render();
    });

    render();
}

// ── 멤버 상세 모달 ──
const detailModal = document.getElementById("memberDetailModal");
const modalUsername = document.getElementById("modal-member-username");
const modalBoardList = document.getElementById("modal-board-list");
const btnCloseDetail = document.getElementById("btn-close-member-detail");

const ROLE_LABEL = { MANAGER: "Manager", USER: "User", REQUESTED: "가입 요청", INVITED: "초대됨" };
const ROLE_CLASS = { MANAGER: "role-manager", USER: "role-user", REQUESTED: "role-requested", INVITED: "role-invited" };

function openDetailModal(memberId, username) {
    modalUsername.textContent = username;
    modalBoardList.innerHTML = "<div class=\"admin-empty\">불러오는 중...</div>";
    detailModal.classList.add("open");
    document.body.style.overflow = "hidden";

    fetch(`/admin/api/members/${memberId}/boards`)
        .then(res => res.json())
        .then(boards => {
            if (boards.length === 0) {
                modalBoardList.innerHTML = "<div class=\"admin-empty\">소속 보드가 없습니다.</div>";
                return;
            }
            const rows = boards.map(b => {
                const label = ROLE_LABEL[b.boardRole] ?? b.boardRole;
                const cls = ROLE_CLASS[b.boardRole] ?? "";
                return `<tr>
                    <td>${b.boardName}</td>
                    <td><span class="role-badge ${cls}">${label}</span></td>
                </tr>`;
            }).join("");
            modalBoardList.innerHTML = `<table class="admin-table">
                <thead><tr><th>보드</th><th style="width:110px;">역할</th></tr></thead>
                <tbody>${rows}</tbody>
            </table>`;
        })
        .catch(() => {
            modalBoardList.innerHTML = "<div class=\"admin-empty\">불러오기 실패</div>";
        });
}

function closeDetailModal() {
    detailModal.classList.remove("open");
    document.body.style.overflow = "";
}

if (tbody) {
    tbody.addEventListener("click", e => {
        const row = e.target.closest("tr[data-member-id]");
        if (row) {
            openDetailModal(row.dataset.memberId, row.dataset.username);
        }
    });
}

if (btnCloseDetail) {
    btnCloseDetail.addEventListener("click", closeDetailModal);
}

if (detailModal) {
    detailModal.addEventListener("click", e => {
        if (e.target === detailModal) {
            closeDetailModal();
        }
    });
}

document.addEventListener("keydown", e => {
    if (e.key === "Escape" && detailModal && detailModal.classList.contains("open")) {
        closeDetailModal();
    }
});