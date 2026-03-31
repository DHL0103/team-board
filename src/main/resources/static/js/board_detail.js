// ── 에러 토스트 ──
window.addEventListener('load', () => {
    const toast = document.getElementById('errorToast');
    if (toast && toast.textContent.trim()) {
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }
});

// 카드 클릭 → 상세 페이지 이동
document.querySelectorAll('.board-card[data-href]').forEach(card => {
    card.addEventListener('click', () => {
        window.location.href = card.dataset.href;
    });
});

// 작업 추가 모달
const createModal = document.getElementById('createModal');
const btnCreatePost = document.getElementById('btn-create-post');
const btnCreateClose = document.getElementById('btn-create-close');

btnCreatePost.addEventListener('click', () => {
    createModal.classList.add('open');
});

function closeCreateModal() {
    createModal.classList.remove('open');
    createFileDataTransfer = new DataTransfer();
    createFileInput.files = createFileDataTransfer.files;
    createFileChipList.innerHTML = '';
    if (btnAddAssignee) {
        selectedAssignees.clear();
        if (selectedAssigneeList) { selectedAssigneeList.innerHTML = ''; }
        if (assigneeSearch) { assigneeSearch.value = ''; }
        if (assigneeDropdown) { assigneeDropdown.classList.remove('open'); }
        if (assigneeOptionList) {
            assigneeOptionList.querySelectorAll('.assignee-option').forEach(o => { o.style.display = ''; });
        }
    }
}

btnCreateClose.addEventListener('click', closeCreateModal);

createModal.addEventListener('click', (e) => {
    if (e.target === createModal) {
        closeCreateModal();
    }
});

// 마감일 피커 (생성 모달)
const createDueValue = document.getElementById('create-due-value');
const createDueDays = document.getElementById('create-due-days');
const createMonthLabel = document.getElementById('create-month-label');
const createSelectedDisplay = document.getElementById('create-selected-display');
const btnCreatePrev = document.getElementById('btn-create-prev-month');
const btnCreateNext = document.getElementById('btn-create-next-month');
const btnCreateClear = document.getElementById('btn-create-clear-due');

let createCurrentDate = new Date();
let createSelectedDate = null;

function renderCreateCalendar() {
    const year = createCurrentDate.getFullYear();
    const month = createCurrentDate.getMonth();
    createMonthLabel.textContent = `${year}년 ${month + 1}월`;

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    createDueDays.innerHTML = '';

    for (let i = 0; i < firstDay; i++) {
        const empty = document.createElement('div');
        empty.classList.add('due-day', 'empty');
        createDueDays.appendChild(empty);
    }

    for (let d = 1; d <= daysInMonth; d++) {
        const day = document.createElement('div');
        day.classList.add('due-day');
        day.textContent = d;

        const date = new Date(year, month, d);
        date.setHours(0, 0, 0, 0);

        if (date < today) {
            day.classList.add('past');
        } else if (date.getTime() === today.getTime()) {
            day.classList.add('today');
        }

        if (createSelectedDate && date.getTime() === createSelectedDate.getTime()) {
            day.classList.add('selected');
        }

        day.addEventListener('click', () => {
            if (day.classList.contains('past')) {
                return;
            }
            createSelectedDate = date;
            const formatted = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
            createDueValue.value = formatted + 'T00:00:00';
            createSelectedDisplay.textContent = `${month + 1}/${d}`;
            renderCreateCalendar();
        });

        createDueDays.appendChild(day);
    }
}

btnCreatePrev.addEventListener('click', () => {
    createCurrentDate.setMonth(createCurrentDate.getMonth() - 1);
    renderCreateCalendar();
});

btnCreateNext.addEventListener('click', () => {
    createCurrentDate.setMonth(createCurrentDate.getMonth() + 1);
    renderCreateCalendar();
});

btnCreateClear.addEventListener('click', () => {
    createSelectedDate = null;
    createDueValue.value = '';
    createSelectedDisplay.textContent = '선택 안 함';
    renderCreateCalendar();
});

// 파일 첨부
let createFileDataTransfer = new DataTransfer();
const createFileInput = document.getElementById('create-file-input');
const btnCreateFileAttach = document.getElementById('btn-create-file-attach');
const createFileChipList = document.getElementById('create-file-chip-list');

btnCreateFileAttach.addEventListener('click', () => {
    createFileInput.click();
});

createFileInput.addEventListener('change', () => {
    for (const file of createFileInput.files) {
        if (file.size > 10 * 1024 * 1024) {
            alert(`${file.name}: 파일 크기는 10MB를 초과할 수 없습니다.`);
            continue;
        }
        createFileDataTransfer.items.add(file);
    }
    createFileInput.files = createFileDataTransfer.files;
    renderCreateFileChips();
});

function renderCreateFileChips() {
    createFileChipList.innerHTML = '';
    for (let i = 0; i < createFileDataTransfer.files.length; i++) {
        const file = createFileDataTransfer.files[i];
        const chip = document.createElement('span');
        chip.className = 'file-chip';
        chip.innerHTML = `${file.name}<button type="button" class="file-chip-remove" data-index="${i}">×</button>`;
        createFileChipList.appendChild(chip);
    }
}

createFileChipList.addEventListener('click', (e) => {
    if (!e.target.classList.contains('file-chip-remove')) return;
    const idx = parseInt(e.target.dataset.index);
    const newDt = new DataTransfer();
    for (let i = 0; i < createFileDataTransfer.files.length; i++) {
        if (i !== idx) newDt.items.add(createFileDataTransfer.files[i]);
    }
    createFileDataTransfer = newDt;
    createFileInput.files = createFileDataTransfer.files;
    renderCreateFileChips();
});

renderCreateCalendar();

// 담당자 선택
const btnAddAssignee = document.getElementById('btn-add-assignee');
const assigneeDropdown = document.getElementById('assignee-dropdown');
const assigneeSearch = document.getElementById('create-assignee-search');
const assigneeOptionList = document.getElementById('assignee-option-list');
const selectedAssigneeList = document.getElementById('selected-assignee-list');
const selectedAssignees = new Map(); // memberId -> username

if (btnAddAssignee) {
    btnAddAssignee.addEventListener('click', (e) => {
        e.stopPropagation();
        assigneeDropdown.classList.toggle('open');
        if (assigneeDropdown.classList.contains('open')) {
            assigneeSearch.focus();
        }
    });
}

document.addEventListener('click', (e) => {
    if (assigneeDropdown && !e.target.closest('#assignee-picker')) {
        assigneeDropdown.classList.remove('open');
    }
});

if (assigneeSearch) {
    assigneeSearch.addEventListener('input', () => {
        const query = assigneeSearch.value.trim().toLowerCase();
        assigneeOptionList.querySelectorAll('.assignee-option').forEach(opt => {
            if (selectedAssignees.has(opt.dataset.memberId)) {
                opt.style.display = 'none';
                return;
            }
            opt.style.display = opt.dataset.username.toLowerCase().includes(query) ? '' : 'none';
        });
    });
}

if (assigneeOptionList) {
    assigneeOptionList.addEventListener('click', (e) => {
        const opt = e.target.closest('.assignee-option');
        if (!opt) { return; }
        selectedAssignees.set(opt.dataset.memberId, opt.dataset.username);
        opt.style.display = 'none';
        renderSelectedAssignees();
        assigneeSearch.value = '';
        assigneeDropdown.classList.remove('open');
    });
}

function renderSelectedAssignees() {
    if (!selectedAssigneeList) { return; }
    selectedAssigneeList.innerHTML = '';
    selectedAssignees.forEach((username, memberId) => {
        const chip = document.createElement('span');
        chip.className = 'selected-assignee-chip';
        chip.innerHTML = `${username}<button type="button" class="selected-assignee-remove" data-member-id="${memberId}">×</button>`;
        selectedAssigneeList.appendChild(chip);
    });
}

if (selectedAssigneeList) {
    selectedAssigneeList.addEventListener('click', (e) => {
        const removeBtn = e.target.closest('.selected-assignee-remove');
        if (!removeBtn) { return; }
        const memberId = removeBtn.dataset.memberId;
        selectedAssignees.delete(memberId);
        const opt = assigneeOptionList.querySelector(`[data-member-id="${memberId}"]`);
        if (opt) { opt.style.display = ''; }
        renderSelectedAssignees();
    });
}

// ── 제목 글자수 카운터 ──
const createTitleInput = document.getElementById('create-title');
const createTitleCount = document.getElementById('createTitleCount');
if (createTitleInput && createTitleCount) {
    createTitleInput.addEventListener('input', () => {
        const len = createTitleInput.value.length;
        createTitleCount.textContent = len + '/100';
        createTitleCount.classList.toggle('near-limit', len >= 90);
    });
}

const createPostForm = document.getElementById('create-post-form');
createPostForm.addEventListener('submit', () => {
    createPostForm.querySelectorAll('input[name="assigneeIds"]').forEach(el => el.remove());
    selectedAssignees.forEach((username, memberId) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'assigneeIds';
        input.value = memberId;
        createPostForm.appendChild(input);
    });
});
