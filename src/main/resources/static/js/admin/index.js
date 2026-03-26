// ── 반려 모달 ──
document.querySelectorAll(".btn-reject").forEach(btn => {
    btn.addEventListener("click", () => {
        document.getElementById("rejectForm").action = "/admin/reject/" + btn.dataset.postId;
        App.openModal("rejectModal");
    });
});

// ── 생성 버튼 — 현재 페이지에 있는 모달만 열기 ──
const btnOpenCreate = document.getElementById("btn-open-create");
if (btnOpenCreate) {
    btnOpenCreate.addEventListener("click", () => {
        if (document.getElementById("createMemberModal")) App.openModal("createMemberModal");
        else if (document.getElementById("createBoardModal")) App.openModal("createBoardModal");
    });
}

// ── 생성 모달 닫기 ──
const btnCloseCreate = document.getElementById("btn-close-create");
if (btnCloseCreate) {
    btnCloseCreate.addEventListener("click", () => {
        if (document.getElementById("createMemberModal")) App.closeModal("createMemberModal");
        else if (document.getElementById("createBoardModal")) App.closeModal("createBoardModal");
    });
}

// ── 수정 모달 닫기 ──
const btnCloseEdit = document.getElementById("btn-close-edit");
if (btnCloseEdit) {
    btnCloseEdit.addEventListener("click", () => {
        if (document.getElementById("editMemberModal")) App.closeModal("editMemberModal");
        else if (document.getElementById("editBoardModal")) App.closeModal("editBoardModal");
    });
}

// ── 멤버 삭제 버튼 ──
document.querySelectorAll(".btn-open-delete-member").forEach(btn => {
    btn.addEventListener("click", () => {
        document.getElementById("delete-member-name").textContent = btn.dataset.username;
        document.getElementById("deleteMemberForm").action = `/admin/member/delete/${btn.dataset.memberId}`;
        App.openModal("deleteMemberModal");
    });
});

const btnDeleteMemberCancel = document.getElementById("btn-delete-member-cancel");
if (btnDeleteMemberCancel) {
    btnDeleteMemberCancel.addEventListener("click", () => App.closeModal("deleteMemberModal"));
}

// ── 수정 버튼 — 데이터 채우고 모달 열기 ──
document.querySelectorAll(".btn-open-edit").forEach(btn => {
    btn.addEventListener("click", () => {

        // members.html
        const editMemberForm = document.getElementById("editMemberForm");
        if (editMemberForm) {
            editMemberForm.action = `/admin/member/update/${btn.dataset.memberId}`;
            const roleSelect = document.getElementById("edit-role-select");
            if (roleSelect) roleSelect.value = btn.dataset.role ?? "ROLE_USER";
            const select = document.getElementById("edit-board-select");
            if (select) select.value = btn.dataset.boardId ?? "";
            App.openModal("editMemberModal");
            return;
        }

        // boards.html
        const editBoardForm = document.getElementById("editBoardForm");
        if (editBoardForm) {
            editBoardForm.action = `/admin/board/update/${btn.dataset.boardId}`;
            document.getElementById("edit-board-name").value = btn.dataset.boardName ?? "";
            document.getElementById("edit-board-slug").value = btn.dataset.boardSlug ?? "";
            document.getElementById("edit-board-desc").value = btn.dataset.boardDesc ?? "";
            App.openModal("editBoardModal");
        }
    });
});