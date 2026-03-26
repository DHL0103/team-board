const boardId = document.body.dataset.boardId;

// ── 반려 모달 ──
document.querySelectorAll(".btn-reject").forEach(btn => {
    btn.addEventListener("click", () => {
        document.getElementById("rejectForm").action =
            "/board/" + boardId + "/manager/requests/reject/" + btn.dataset.postId;
        openModal("rejectModal");
    });
});
