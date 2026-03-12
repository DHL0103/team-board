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

// ── 역할 토글 (User ↔ Suspended) ──
const roleChangeModal = document.getElementById("roleChangeModal");
const roleChangeTitle = document.getElementById("role-change-title");
const roleChangeDesc = document.getElementById("role-change-desc");
const btnRoleChangeConfirm = document.getElementById("btn-role-change-confirm");
const btnRoleChangeCancel = document.getElementById("btn-role-change-cancel");

let pendingRoleChange = null;

if (tbody) {
    tbody.addEventListener("click", e => {
        const badge = e.target.closest(".role-badge--toggle");
        if (!badge) {
            return;
        }
        e.stopPropagation();

        const row = badge.closest("tr[data-member-id]");
        const memberId = row.dataset.memberId;
        const username = row.dataset.username;
        const currentRole = row.dataset.role;
        const newRole = currentRole === "ROLE_USER" ? "ROLE_SUSPENDED" : "ROLE_USER";

        pendingRoleChange = { memberId, newRole, row, badge };

        if (newRole === "ROLE_SUSPENDED") {
            roleChangeTitle.textContent = "멤버 정지";
            roleChangeDesc.textContent = `${username}님을 정지하시겠습니까?`;
        } else {
            roleChangeTitle.textContent = "정지 해제";
            roleChangeDesc.textContent = `${username}님의 정지를 해제하시겠습니까?`;
        }

        roleChangeModal.classList.add("open");
        document.body.style.overflow = "hidden";
    });
}

if (btnRoleChangeConfirm) {
    btnRoleChangeConfirm.addEventListener("click", () => {
        if (!pendingRoleChange) {
            return;
        }
        const { memberId, newRole, row, badge } = pendingRoleChange;

        fetch(`/admin/api/members/${memberId}/role?role=${newRole}`, { method: "POST" })
            .then(res => {
                if (!res.ok) {
                    return;
                }
                row.dataset.role = newRole;
                if (newRole === "ROLE_SUSPENDED") {
                    badge.className = "role-badge role-suspended role-badge--toggle";
                    badge.textContent = "정지";
                    badge.title = "클릭하여 복구";
                } else {
                    badge.className = "role-badge role-user role-badge--toggle";
                    badge.textContent = "User";
                    badge.title = "클릭하여 정지";
                }
            })
            .finally(() => {
                roleChangeModal.classList.remove("open");
                document.body.style.overflow = "";
                pendingRoleChange = null;
            });
    });
}

if (btnRoleChangeCancel) {
    btnRoleChangeCancel.addEventListener("click", () => {
        roleChangeModal.classList.remove("open");
        document.body.style.overflow = "";
        pendingRoleChange = null;
    });
}

if (roleChangeModal) {
    roleChangeModal.addEventListener("click", e => {
        if (e.target === roleChangeModal) {
            roleChangeModal.classList.remove("open");
            document.body.style.overflow = "";
            pendingRoleChange = null;
        }
    });
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
}

if (tbody) {
    tbody.addEventListener("click", e => {
        if (e.target.closest(".role-badge--toggle")) {
            return;
        }
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