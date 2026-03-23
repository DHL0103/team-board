const PAGE_SIZE = 10;

const tbody = document.getElementById("member-tbody");
const searchInput = document.getElementById("member-search");
const paginationWrap = document.getElementById("pagination-wrap");

const btnFilterActive = document.getElementById("btn-filter-active");
const btnFilterSuspended = document.getElementById("btn-filter-suspended");

if (tbody) {
    const allRows = Array.from(tbody.querySelectorAll("tr"));
    let filteredRows = [];
    let currentPage = 1;
    let currentFilter = "active";

    function applyFilters() {
        const query = searchInput.value.trim().toLowerCase();
        filteredRows = allRows.filter(row => {
            const role = row.dataset.role;
            const matchesFilter = currentFilter === "active"
                ? role !== "ROLE_SUSPENDED"
                : role === "ROLE_SUSPENDED";
            const matchesSearch = row.dataset.username.toLowerCase().includes(query);
            return matchesFilter && matchesSearch;
        });
        currentPage = 1;
        render();
    }

    function render() {
        const start = (currentPage - 1) * PAGE_SIZE;
        const end = start + PAGE_SIZE;
        allRows.forEach(row => { row.style.display = "none"; });
        filteredRows.slice(start, end).forEach((row, i) => {
            row.style.display = "";
            const numCell = row.querySelector(".row-num");
            if (numCell) { numCell.textContent = start + i + 1; }
        });
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

    if (btnFilterActive) {
        btnFilterActive.addEventListener("click", () => {
            currentFilter = "active";
            btnFilterActive.classList.add("active");
            btnFilterSuspended.classList.remove("active");
            applyFilters();
        });
    }

    if (btnFilterSuspended) {
        btnFilterSuspended.addEventListener("click", () => {
            currentFilter = "suspended";
            btnFilterSuspended.classList.add("active");
            btnFilterActive.classList.remove("active");
            applyFilters();
        });
    }

    searchInput.addEventListener("input", () => { applyFilters(); });

    applyFilters();
}

// ── 멤버 상세 모달 ──
const detailModal = document.getElementById("memberDetailModal");
const modalUsername = document.getElementById("modal-member-username");
const modalBoardList = document.getElementById("modal-board-list");
const btnCloseDetail = document.getElementById("btn-close-member-detail");
const btnRoleAction = document.getElementById("btn-member-role-action");

const ROLE_LABEL = { MANAGER: "Manager", USER: "User", REQUESTED: "가입 요청", INVITED: "초대됨" };
const ROLE_CLASS = { MANAGER: "role-manager", USER: "role-user", REQUESTED: "role-requested", INVITED: "role-invited" };

let currentDetailRow = null;

function openDetailModal(row) {
    const memberId = row.dataset.memberId;
    const username = row.dataset.username;
    const role = row.dataset.role;

    currentDetailRow = row;
    modalUsername.textContent = username;
    modalBoardList.innerHTML = "<div class=\"admin-empty\">불러오는 중...</div>";

    if (role === "ROLE_USER") {
        btnRoleAction.textContent = "정지하기";
        btnRoleAction.className = "btn-admin-danger";
        btnRoleAction.style.display = "";
    } else if (role === "ROLE_SUSPENDED") {
        btnRoleAction.textContent = "복구하기";
        btnRoleAction.className = "btn-admin-edit";
        btnRoleAction.style.display = "";
    } else {
        btnRoleAction.style.display = "none";
    }

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
                    <td><a href="/board/${b.boardId}" class="admin-board-link">${b.boardName}</a></td>
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
    currentDetailRow = null;
}

if (tbody) {
    tbody.addEventListener("click", e => {
        const row = e.target.closest("tr[data-member-id]");
        if (row) {
            openDetailModal(row);
        }
    });
}

if (btnRoleAction) {
    btnRoleAction.addEventListener("click", () => {
        if (!currentDetailRow) {
            return;
        }
        const memberId = currentDetailRow.dataset.memberId;
        const currentRole = currentDetailRow.dataset.role;
        const newRole = currentRole === "ROLE_USER" ? "ROLE_SUSPENDED" : "ROLE_USER";

        fetch(`/admin/api/members/${memberId}/role?role=${newRole}`, { method: "POST" })
            .then(res => {
                if (!res.ok) {
                    return;
                }
                currentDetailRow.dataset.role = newRole;
                const badge = currentDetailRow.querySelector(".role-badge");
                if (newRole === "ROLE_SUSPENDED") {
                    badge.className = "role-badge role-suspended";
                    badge.textContent = "정지";
                } else {
                    badge.className = "role-badge role-user";
                    badge.textContent = "User";
                }
                closeDetailModal();
                applyFilters();
            });
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
