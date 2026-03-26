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
    createFileManager.reset();
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
initDatePicker('create', { formatDisplay: (m, d) => `${parseInt(m)}/${parseInt(d)}` });

// 파일 첨부
const createFileManager = initFileAttachment('create-file-input', 'btn-create-file-attach', 'create-file-chip-list');

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
initCharCounter(
    document.getElementById('create-title'),
    document.getElementById('createTitleCount'),
    100, 90
);

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
