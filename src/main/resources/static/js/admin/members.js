const PAGE_SIZE = 10;

const tbody          = document.getElementById("member-tbody");
const memberTable    = document.getElementById("member-table");
const memberEmpty    = document.getElementById("member-empty");
const countMeta      = document.getElementById("member-count-meta");
const searchInput    = document.getElementById("member-search");
const paginationWrap = document.getElementById("pagination-wrap");
const btnFilterActive    = document.getElementById("btn-filter-active");
const btnFilterSuspended = document.getElementById("btn-filter-suspended");

let currentPage   = 1;
let currentFilter = "active";
let debounceTimer = null;

const ROLE_BADGE = {
    ROLE_ADMIN:     '<span class="role-badge role-admin">Admin</span>',
    ROLE_USER:      '<span class="role-badge role-user">User</span>',
    ROLE_SUSPENDED: '<span class="role-badge role-suspended">정지</span>',
};

function createRow(member, index) {
    const tr = document.createElement("tr");
    tr.className = "admin-table-row";
    tr.dataset.memberId = member.id;
    tr.dataset.username = member.username;
    tr.dataset.role     = member.role;
    tr.innerHTML =
        `<td class="row-num">${(currentPage - 1) * PAGE_SIZE + index + 1}</td>` +
        `<td>${member.username}</td>` +
        `<td>${ROLE_BADGE[member.role] ?? member.role}</td>`;
    return tr;
}

async function fetchAndRender() {
    const params = new URLSearchParams({
        q:      searchInput ? searchInput.value.trim() : "",
        filter: currentFilter,
        page:   currentPage - 1,
        size:   PAGE_SIZE,
    });

    try {
        const res  = await fetch(`/api/admin/members?${params}`);
        const data = await res.json();

        if (countMeta) { countMeta.textContent = data.totalCount + "명"; }

        tbody.innerHTML = "";

        if (data.members.length === 0) {
            memberEmpty.style.display = "";
            memberTable.style.display = "none";
        } else {
            memberEmpty.style.display = "none";
            memberTable.style.display = "";
            data.members.forEach((member, i) => tbody.appendChild(createRow(member, i)));
        }

        const totalPages = Math.max(1, Math.ceil(data.totalCount / PAGE_SIZE));
        App.renderPagination(paginationWrap, totalPages, currentPage, page => {
            currentPage = page;
            fetchAndRender();
        });
    } catch (e) {
        console.error("멤버 목록 로드 실패", e);
    }
}

if (btnFilterActive) {
    btnFilterActive.addEventListener("click", () => {
        currentFilter = "active";
        currentPage   = 1;
        btnFilterActive.classList.add("active");
        btnFilterSuspended.classList.remove("active");
        fetchAndRender();
    });
}

if (btnFilterSuspended) {
    btnFilterSuspended.addEventListener("click", () => {
        currentFilter = "suspended";
        currentPage   = 1;
        btnFilterSuspended.classList.add("active");
        btnFilterActive.classList.remove("active");
        fetchAndRender();
    });
}

if (searchInput) {
    searchInput.addEventListener("input", () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            currentPage = 1;
            fetchAndRender();
        }, 250);
    });
}

fetchAndRender();

// ── 멤버 상세 모달 ──
const detailModal    = document.getElementById("memberDetailModal");
const modalUsername  = document.getElementById("modal-member-username");
const modalBoardList = document.getElementById("modal-board-list");
const btnCloseDetail = document.getElementById("btn-close-member-detail");
const btnRoleAction  = document.getElementById("btn-member-role-action");

const ROLE_LABEL = { MANAGER: "Manager", USER: "User", REQUESTED: "가입 요청", INVITED: "초대됨" };
const ROLE_CLASS  = { MANAGER: "role-manager", USER: "role-user", REQUESTED: "role-requested", INVITED: "role-invited" };

let currentDetailRow = null;

function openDetailModal(row) {
    const memberId = row.dataset.memberId;
    const username = row.dataset.username;
    const role     = row.dataset.role;

    currentDetailRow = row;
    modalUsername.textContent = username;
    modalBoardList.innerHTML  = '<div class="admin-empty">불러오는 중...</div>';

    if (role === "ROLE_USER") {
        btnRoleAction.textContent = "정지하기";
        btnRoleAction.className   = "btn-admin-danger";
        btnRoleAction.style.display = "";
    } else if (role === "ROLE_SUSPENDED") {
        btnRoleAction.textContent = "복구하기";
        btnRoleAction.className   = "btn-admin-edit";
        btnRoleAction.style.display = "";
    } else {
        btnRoleAction.style.display = "none";
    }

    App.openModal("memberDetailModal");

    fetch(`/api/admin/members/${memberId}/boards`)
        .then(res => res.json())
        .then(boards => {
            if (boards.length === 0) {
                modalBoardList.innerHTML = '<div class="admin-empty">소속 보드가 없습니다.</div>';
                return;
            }
            const rows = boards.map(b => {
                const label = ROLE_LABEL[b.boardRole] ?? b.boardRole;
                const cls   = ROLE_CLASS[b.boardRole] ?? "";
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
            modalBoardList.innerHTML = '<div class="admin-empty">불러오기 실패</div>';
        });
}

function closeDetailModal() {
    App.closeModal("memberDetailModal");
    currentDetailRow = null;
}

if (tbody) {
    tbody.addEventListener("click", e => {
        const row = e.target.closest("tr[data-member-id]");
        if (row) { openDetailModal(row); }
    });
}

if (btnRoleAction) {
    btnRoleAction.addEventListener("click", () => {
        if (!currentDetailRow) { return; }
        const memberId    = currentDetailRow.dataset.memberId;
        const currentRole = currentDetailRow.dataset.role;
        const newRole     = currentRole === "ROLE_USER" ? "ROLE_SUSPENDED" : "ROLE_USER";

        fetch(`/api/admin/members/${memberId}/role?role=${newRole}`, { method: "POST" })
            .then(res => {
                if (!res.ok) { return; }
                closeDetailModal();
                fetchAndRender();
            });
    });
}

if (btnCloseDetail) {
    btnCloseDetail.addEventListener("click", closeDetailModal);
}

if (detailModal) {
    detailModal.addEventListener("click", e => {
        if (e.target === detailModal) { closeDetailModal(); }
    });
}

document.addEventListener("keydown", e => {
    if (e.key === "Escape" && detailModal && detailModal.classList.contains("open")) {
        closeDetailModal();
    }
});
