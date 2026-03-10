const boardId = document.body.dataset.boardId;

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
        if (e.target === overlay) { closeModal(overlay.id); }
    });
});

document.querySelectorAll(".modal-close").forEach(btn => {
    btn.addEventListener("click", () => {
        const overlay = btn.closest(".modal-overlay");
        if (overlay) { closeModal(overlay.id); }
    });
});

// ── 반려 모달 ──
document.querySelectorAll(".btn-reject").forEach(btn => {
    btn.addEventListener("click", () => {
        document.getElementById("rejectForm").action =
            "/board/" + boardId + "/manager/requests/reject/" + btn.dataset.postId;
        openModal("rejectModal");
    });
});

document.addEventListener("keydown", e => {
    if (e.key === "Escape") {
        document.querySelectorAll(".modal-overlay.open").forEach(m => closeModal(m.id));
    }
});
