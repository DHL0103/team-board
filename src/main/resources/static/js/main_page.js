// ── 기본 초기화 ──
const today = new Date();
today.setHours(0, 0, 0, 0);

const colMap = {
    backend: { bodyId: "body-backend", countId: "count-backend" },
    frontend: { bodyId: "body-frontend", countId: "count-frontend", emptyId: "empty-frontend" },
    design: { bodyId: "body-design", countId: "count-design", emptyId: "empty-design" },
};

Object.values(colMap).forEach(({ bodyId, countId, emptyId }) => {
    const body = document.getElementById(bodyId);
    if (!body) return;
    const cards = body.querySelectorAll(".board-card");
    let visible = 0;
    cards.forEach(card => { if (card.dataset.status === "COMPLETED") card.style.display = "none"; else visible++; });
    document.getElementById(countId).textContent = visible + "개";
    if (emptyId && visible === 0) document.getElementById(emptyId).style.display = "flex";
});

document.querySelectorAll(".due-chip").forEach(chip => {
    const due = chip.dataset.due;
    if (!due) { chip.classList.add("none"); return; }
    const dueDate = new Date(due);
    const diffDays = Math.floor((dueDate - today) / (1000 * 60 * 60 * 24));
    const label = chip.querySelector(".due-label");
    if (diffDays < 0) { chip.classList.add("over"); label.textContent += " 초과"; }
    else if (diffDays === 0) { chip.classList.add("warn"); label.textContent = "오늘 마감"; }
    else if (diffDays === 1) { chip.classList.add("warn"); label.textContent = "내일 마감"; }
    else if (diffDays <= 3) { chip.classList.add("warn"); }
    else { chip.classList.add("safe"); }
});

document.querySelectorAll(".status-chip").forEach(chip => {
    const s = chip.dataset.status;
    if (s === "PROGRESS") chip.classList.add("progress");
    if (s === "REQUESTED") chip.classList.add("requested");
});

const boardUrlMap = { "1": "backend", "2": "frontend", "3": "design" };
document.querySelectorAll(".board-card").forEach(card => {
    card.addEventListener("mouseenter", () => card.classList.add("is-hovered"));
    card.addEventListener("mouseleave", () => card.classList.remove("is-hovered"));
    card.addEventListener("click", () => {
        const postId = card.dataset.id;
        if (postId) location.href = `/post/${postId}`;
    });
});

document.querySelectorAll(".board-card").forEach(async card => {
    if (card.dataset.status === "COMPLETED") return;
    const memberId = card.dataset.memberId;
    if (!memberId) return;
    const name = await fetchUsername(memberId);
    card.querySelector(".writer-name").textContent = name;
});

document.querySelectorAll(".view-tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".view-tab").forEach(t => t.classList.remove("active"));
        tab.classList.add("active");
    });
});

// ── 모달 ──
const teamStyles = {
    backend: { bg: "var(--backend-bg)", color: "var(--backend-color)", border: "var(--backend-border)" },
    frontend: { bg: "var(--frontend-bg)", color: "var(--frontend-color)", border: "var(--frontend-border)" },
    design: { bg: "var(--design-bg)", color: "var(--design-color)", border: "var(--design-border)" },
};

const createModal = document.getElementById("createModal");

function openModal(boardId, teamName, teamKey) {
    document.getElementById("modal-board-id").value = boardId;
    const badge = document.getElementById("modal-team-badge");
    const s = teamStyles[teamKey];
    badge.textContent = teamName;
    badge.style.background = s.bg;
    badge.style.color = s.color;
    badge.style.border = `1px solid ${s.border}`;
    clearDue();
    renderCalendar();
    createModal.classList.add("open");
    document.body.style.overflow = "hidden";
}

function closeModal() {
    createModal.classList.remove("open");
    document.body.style.overflow = "";
    // 파일 첨부 초기화
    fileDataTransfer = new DataTransfer();
    fileInput.files = fileDataTransfer.files;
    fileChipList.innerHTML = "";
}

document.getElementById("btn-add-backend").addEventListener("click", () => openModal(1, "Backend팀", "backend"));
document.getElementById("btn-add-frontend").addEventListener("click", () => openModal(2, "Frontend팀", "frontend"));
document.getElementById("btn-add-design").addEventListener("click", () => openModal(3, "Design팀", "design"));

createModal.addEventListener("click", (e) => {
    if (e.target === createModal) closeModal();
});

document.getElementById("btn-modal-close").addEventListener("click", closeModal);

document.addEventListener("keydown", e => { if (e.key === "Escape") closeModal(); });

// ── 날짜 피커 ──
let pickerYear = today.getFullYear();
let pickerMonth = today.getMonth();
let selectedDate = null;

function changeMonth(dir) {
    pickerMonth += dir;
    if (pickerMonth < 0) { pickerMonth = 11; pickerYear--; }
    if (pickerMonth > 11) { pickerMonth = 0; pickerYear++; }
    renderCalendar();
}

document.getElementById("btn-prev-month").addEventListener("click", () => changeMonth(-1));
document.getElementById("btn-next-month").addEventListener("click", () => changeMonth(1));

function renderCalendar() {
    const label = document.getElementById("due-month-label");
    label.textContent = `${pickerYear}년 ${pickerMonth + 1}월`;

    const grid = document.getElementById("due-days");
    grid.innerHTML = "";

    const firstDay = new Date(pickerYear, pickerMonth, 1).getDay();
    const lastDate = new Date(pickerYear, pickerMonth + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        const el = document.createElement("div");
        el.className = "due-day empty";
        grid.appendChild(el);
    }

    for (let d = 1; d <= lastDate; d++) {
        const el = document.createElement("div");
        el.className = "due-day";
        el.textContent = d;

        const thisDate = new Date(pickerYear, pickerMonth, d);
        const isToday = thisDate.getTime() === today.getTime();
        const isPast = thisDate < today;

        if (isPast) el.classList.add("past");
        if (isToday) el.classList.add("today");
        if (selectedDate && thisDate.getTime() === selectedDate.getTime()) el.classList.add("selected");

        if (!isPast) {
            el.addEventListener("click", () => selectDate(thisDate));
        }
        grid.appendChild(el);
    }
}

function selectDate(date) {
    selectedDate = date;
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    document.getElementById("modal-due-value").value = `${y}-${m}-${d}T00:00:00`;
    document.getElementById("due-selected-display").textContent = `${m}.${d} 마감`;
    renderCalendar();
}

function clearDue() {
    selectedDate = null;
    document.getElementById("modal-due-value").value = "";
    document.getElementById("due-selected-display").textContent = "선택 안 함";
    renderCalendar();
}

document.getElementById("btn-clear-due").addEventListener("click", () => clearDue());

renderCalendar();

// ── 파일 첨부 ──
// FileList는 read-only이므로 DataTransfer로 관리
let fileDataTransfer = new DataTransfer();
const fileInput = document.getElementById("file-input");
const fileChipList = document.getElementById("file-chip-list");

document.getElementById("btn-file-attach").addEventListener("click", () => {
    fileInput.click();
});

fileInput.addEventListener("change", () => {
    for (const file of fileInput.files) {
        if (file.size > 10 * 1024 * 1024) {
            showToast(`${file.name}: 파일 크기는 10MB를 초과할 수 없습니다.`);
            continue;
        }
        fileDataTransfer.items.add(file);
    }
    fileInput.files = fileDataTransfer.files;
    renderFileChips();
});

document.addEventListener("DOMContentLoaded", () => {
    const toast = document.getElementById("errorToast");
    if (toast) {
        toast.classList.add("show");
        setTimeout(() => toast.classList.remove("show"), 3000);
    }
});

function renderFileChips() {
    fileChipList.innerHTML = "";
    for (let i = 0; i < fileDataTransfer.files.length; i++) {
        const file = fileDataTransfer.files[i];
        const chip = document.createElement("span");
        chip.className = "file-chip";
        chip.innerHTML = `${file.name}<button type="button" class="file-chip-remove" data-index="${i}">×</button>`;
        fileChipList.appendChild(chip);
    }
}

fileChipList.addEventListener("click", (e) => {
    if (!e.target.classList.contains("file-chip-remove")) return;
    const idx = parseInt(e.target.dataset.index);
    const newDt = new DataTransfer();
    for (let i = 0; i < fileDataTransfer.files.length; i++) {
        if (i !== idx) newDt.items.add(fileDataTransfer.files[i]);
    }
    fileDataTransfer = newDt;
    fileInput.files = fileDataTransfer.files;
    renderFileChips();
});