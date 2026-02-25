// ── 모달 유틸 ──
function openModal(id) {
    document.getElementById(id).classList.add("open");
    document.body.style.overflow = "hidden";
}

function closeModal(id) {
    document.getElementById(id).classList.remove("open");
    document.body.style.overflow = "";
}

document.querySelectorAll(".modal-overlay").forEach(overlay => {
    overlay.addEventListener("click", e => {
        if (e.target === overlay) closeModal(overlay.id);
    });
});

document.addEventListener("keydown", e => {
    if (e.key === "Escape") {
        document.querySelectorAll(".modal-overlay.open").forEach(m => closeModal(m.id));
    }
});

// ── 생성 버튼 — 현재 페이지에 있는 모달만 열기 ──
const btnOpenCreate = document.getElementById("btn-open-create");
if (btnOpenCreate) {
    btnOpenCreate.addEventListener("click", () => {
        if (document.getElementById("createMemberModal")) openModal("createMemberModal");
        else if (document.getElementById("createBoardModal")) openModal("createBoardModal");
    });
}

// ── 생성 모달 닫기 ──
const btnCloseCreate = document.getElementById("btn-close-create");
if (btnCloseCreate) {
    btnCloseCreate.addEventListener("click", () => {
        if (document.getElementById("createMemberModal")) closeModal("createMemberModal");
        else if (document.getElementById("createBoardModal")) closeModal("createBoardModal");
    });
}

// ── 수정 모달 닫기 ──
const btnCloseEdit = document.getElementById("btn-close-edit");
if (btnCloseEdit) {
    btnCloseEdit.addEventListener("click", () => {
        if (document.getElementById("editMemberModal")) closeModal("editMemberModal");
        else if (document.getElementById("editBoardModal")) closeModal("editBoardModal");
    });
}

// ── 수정 버튼 — 데이터 채우고 모달 열기 ──
document.querySelectorAll(".btn-open-edit").forEach(btn => {
    btn.addEventListener("click", () => {

        // members.html
        const editMemberForm = document.getElementById("editMemberForm");
        if (editMemberForm) {
            editMemberForm.action = `/admin/user/update/${btn.dataset.memberId}`;
            const select = document.getElementById("edit-board-select");
            if (select) select.value = btn.dataset.boardId ?? "";
            openModal("editMemberModal");
            return;
        }

        // boards.html
        const editBoardForm = document.getElementById("editBoardForm");
        if (editBoardForm) {
            editBoardForm.action = `/admin/board/update/${btn.dataset.boardId}`;
            document.getElementById("edit-board-name").value = btn.dataset.boardName ?? "";
            document.getElementById("edit-board-slug").value = btn.dataset.boardSlug ?? "";
            document.getElementById("edit-board-desc").value = btn.dataset.boardDesc ?? "";
            openModal("editBoardModal");
        }
    });
});