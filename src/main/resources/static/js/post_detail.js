// ── 수정 모달 ──
const editModal = document.getElementById("editModal");
const btnEdit = document.getElementById("btn-edit");
const btnEditClose = document.getElementById("btn-edit-close");

function closeEditModal() {
    editModal.classList.remove("open");
    document.body.style.overflow = "";
}

if (btnEdit) {
    btnEdit.addEventListener("click", () => {
        editModal.classList.add("open");
        document.body.style.overflow = "hidden";
    });
}
if (btnEditClose) {
    btnEditClose.addEventListener("click", closeEditModal);
}
if (editModal) {
    editModal.addEventListener("click", (e) => {
        if (e.target.id === "editModal") closeEditModal();
    });
}

// ── 삭제 모달 ──
const deleteModal = document.getElementById("deleteModal");
const btnDelete = document.getElementById("btn-delete");
const btnDeleteCancel = document.getElementById("btn-delete-cancel");

if (btnDelete) {
    btnDelete.addEventListener("click", () => {
        deleteModal.classList.add("open");
        document.body.style.overflow = "hidden";
    });
}
if (btnDeleteCancel) {
    btnDeleteCancel.addEventListener("click", () => {
        deleteModal.classList.remove("open");
        document.body.style.overflow = "";
    });
}
if (deleteModal) {
    deleteModal.addEventListener("click", (e) => {
        if (e.target.id === "deleteModal") {
            deleteModal.classList.remove("open");
            document.body.style.overflow = "";
        }
    });
}

// ── 작성자 이름 Fetch ──
fetchUsername(memberId).then(name => {
    document.getElementById("writer-name").textContent = name;
});

// ── 에러 토스트 ──
window.addEventListener("load", () => {
    const toast = document.getElementById("errorToast");
    if (toast) {
        toast.classList.add("show");
        setTimeout(() => toast.classList.remove("show"), 3000);
    }
});

// ── 수정 모달 캘린더 피커 ──
const editToday = new Date();
editToday.setHours(0, 0, 0, 0);

let editPickerYear = editToday.getFullYear();
let editPickerMonth = editToday.getMonth();
let editSelectedDate = null;

if (existingDueDateStr) {
    editSelectedDate = new Date(existingDueDateStr + 'T00:00:00');
    editPickerYear = editSelectedDate.getFullYear();
    editPickerMonth = editSelectedDate.getMonth();
    const m = String(editSelectedDate.getMonth() + 1).padStart(2, '0');
    const d = String(editSelectedDate.getDate()).padStart(2, '0');
    document.getElementById('edit-selected-display').textContent = `${m}.${d} 마감`;
}

function editChangeMonth(dir) {
    editPickerMonth += dir;
    if (editPickerMonth < 0) { editPickerMonth = 11; editPickerYear--; }
    if (editPickerMonth > 11) { editPickerMonth = 0; editPickerYear++; }
    renderEditCalendar();
}

document.getElementById('btn-edit-prev-month').addEventListener('click', () => editChangeMonth(-1));
document.getElementById('btn-edit-next-month').addEventListener('click', () => editChangeMonth(1));

function renderEditCalendar() {
    document.getElementById('edit-month-label').textContent = `${editPickerYear}년 ${editPickerMonth + 1}월`;
    const grid = document.getElementById('edit-due-days');
    grid.innerHTML = '';

    const firstDay = new Date(editPickerYear, editPickerMonth, 1).getDay();
    const lastDate = new Date(editPickerYear, editPickerMonth + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        const el = document.createElement('div');
        el.className = 'due-day empty';
        grid.appendChild(el);
    }

    for (let d = 1; d <= lastDate; d++) {
        const el = document.createElement('div');
        el.className = 'due-day';
        el.textContent = d;

        const thisDate = new Date(editPickerYear, editPickerMonth, d);
        const isToday = thisDate.getTime() === editToday.getTime();
        const isPast = thisDate < editToday;

        if (isPast) el.classList.add('past');
        if (isToday) el.classList.add('today');
        if (editSelectedDate && thisDate.getTime() === editSelectedDate.getTime()) el.classList.add('selected');

        if (!isPast) {
            el.addEventListener('click', () => editSelectDate(thisDate));
        }
        grid.appendChild(el);
    }
}

function editSelectDate(date) {
    editSelectedDate = date;
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    document.getElementById('edit-due-value').value = `${y}-${m}-${d}T00:00:00`;
    document.getElementById('edit-selected-display').textContent = `${m}.${d} 마감`;
    renderEditCalendar();
}

function clearEditDue() {
    editSelectedDate = null;
    document.getElementById('edit-due-value').value = '';
    document.getElementById('edit-selected-display').textContent = '선택 안 함';
    renderEditCalendar();
}

document.getElementById('btn-edit-clear-due').addEventListener('click', clearEditDue);

renderEditCalendar();
