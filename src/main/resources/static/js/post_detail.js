// ── 수정 모달 ──
const editModal = document.getElementById("editModal");
const btnEdit = document.getElementById("btn-edit");
const btnEditClose = document.getElementById("btn-edit-close");

function closeEditModal() {
    editModal.classList.remove("open");
    document.body.style.overflow = "";
    editFileDataTransfer = new DataTransfer();
    if (editFileInput) editFileInput.files = editFileDataTransfer.files;
    if (editFileChipList) editFileChipList.innerHTML = '';
    if (btnEditAddAssignee) {
        editSelectedAssignees.clear();
        editOriginalAssignees.forEach((username, memberId) => editSelectedAssignees.set(memberId, username));
        renderEditSelectedAssignees();
        if (editAssigneeOptionList) {
            editAssigneeOptionList.querySelectorAll('.assignee-option').forEach(o => {
                o.style.display = editSelectedAssignees.has(o.dataset.memberId) ? 'none' : '';
            });
        }
        if (editAssigneeSearch) { editAssigneeSearch.value = ''; }
        if (editAssigneeDropdown) { editAssigneeDropdown.classList.remove('open'); }
    }
}

const editTitleInput = document.getElementById("edit-title");
const editTitleCount = document.getElementById("editTitleCount");
if (editTitleInput && editTitleCount) {
    editTitleInput.addEventListener("input", () => {
        const len = editTitleInput.value.length;
        editTitleCount.textContent = len + "/100";
        editTitleCount.classList.toggle("near-limit", len >= 90);
    });
}

if (btnEdit) {
    btnEdit.addEventListener("click", () => {
        editModal.classList.add("open");
        document.body.style.overflow = "hidden";
        if (editTitleInput && editTitleCount) {
            editTitleCount.textContent = editTitleInput.value.length + "/100";
        }
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

// ── 반려 모달 ──
const rejectModal = document.getElementById("rejectModal");
const btnReject = document.querySelector(".btn-reject");
const btnRejectClose = document.getElementById("btn-reject-close");
const boardId = document.body.dataset.boardId;

const rejectReasonTextarea = document.getElementById("rejectReason");
const rejectReasonCount = document.getElementById("rejectReasonCount");
if (rejectReasonTextarea && rejectReasonCount) {
    rejectReasonTextarea.addEventListener("input", () => {
        const len = rejectReasonTextarea.value.length;
        rejectReasonCount.textContent = len + "/300";
        rejectReasonCount.classList.toggle("near-limit", len >= 270);
    });
}

if (btnReject) {
    btnReject.addEventListener("click", () => {
        document.getElementById("rejectForm").action =
            "/board/" + boardId + "/manager/requests/reject/" + btnReject.dataset.postId;
        rejectModal.classList.add("open");
        document.body.style.overflow = "hidden";
    });
}
if (btnRejectClose) {
    btnRejectClose.addEventListener("click", () => {
        rejectModal.classList.remove("open");
        document.body.style.overflow = "";
    });
}
if (rejectModal) {
    rejectModal.addEventListener("click", (e) => {
        if (e.target.id === "rejectModal") {
            rejectModal.classList.remove("open");
            document.body.style.overflow = "";
        }
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

const memberId = parseInt(document.body.dataset.memberId);
const existingDueDateStr = document.body.dataset.dueDate || '';

// ── 작성자 이름 Fetch ──
fetchUsername(memberId).then(name => {
    document.getElementById("writer-name").textContent = name;
});

// ── 반려자 이름 Fetch ──
document.querySelectorAll('.rejection-item').forEach(async item => {
    const rejectedBy = item.dataset.rejectedBy;
    const span = item.querySelector('.rejection-rejector');
    if (!rejectedBy) {
        span.textContent = '';
        return;
    }
    const name = await fetchUsername(rejectedBy);
    span.textContent = name;
});

// ── 반려 사유 더보기 ──
document.querySelectorAll('.rejection-reason').forEach(el => {
    const fullHeight = el.scrollHeight;
    el.classList.add('clamped');
    if (fullHeight > el.clientHeight) {
        const btn = document.createElement('button');
        btn.className = 'rejection-reason-toggle';
        btn.textContent = '더보기';
        btn.addEventListener('click', () => {
            const expanded = el.classList.toggle('expanded');
            el.classList.toggle('clamped', !expanded);
            btn.textContent = expanded ? '접기' : '더보기';
        });
        el.insertAdjacentElement('afterend', btn);
    }
});

// ── 에러 토스트 ──
window.addEventListener("load", () => {
    const toast = document.getElementById("errorToast");
    if (toast && toast.textContent.trim()) {
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

// ── 수정 모달 파일 첨부 ──
let editFileDataTransfer = new DataTransfer();
const editFileInput = document.getElementById('edit-file-input');
const editFileChipList = document.getElementById('edit-file-chip-list');

if (editFileInput) {
    document.getElementById('btn-edit-file-attach').addEventListener('click', () => {
        editFileInput.click();
    });

    editFileInput.addEventListener('change', () => {
        for (const file of editFileInput.files) {
            if (file.size > 10 * 1024 * 1024) {
                showToast(`${file.name}: 파일 크기는 10MB를 초과할 수 없습니다.`);
                continue;
            }
            editFileDataTransfer.items.add(file);
        }
        editFileInput.files = editFileDataTransfer.files;
        renderEditFileChips();
    });
}

function renderEditFileChips() {
    editFileChipList.innerHTML = '';
    for (let i = 0; i < editFileDataTransfer.files.length; i++) {
        const file = editFileDataTransfer.files[i];
        const chip = document.createElement('span');
        chip.className = 'file-chip';
        chip.innerHTML = `${file.name}<button type="button" class="file-chip-remove" data-index="${i}">×</button>`;
        editFileChipList.appendChild(chip);
    }
}

if (editFileChipList) {
    editFileChipList.addEventListener('click', (e) => {
        if (!e.target.classList.contains('file-chip-remove')) return;
        const idx = parseInt(e.target.dataset.index);
        const newDt = new DataTransfer();
        for (let i = 0; i < editFileDataTransfer.files.length; i++) {
            if (i !== idx) newDt.items.add(editFileDataTransfer.files[i]);
        }
        editFileDataTransfer = newDt;
        editFileInput.files = editFileDataTransfer.files;
        renderEditFileChips();
    });
}

// ── 수정 모달 담당자 선택 ──
const btnEditAddAssignee = document.getElementById('btn-edit-add-assignee');
const editAssigneeDropdown = document.getElementById('edit-assignee-dropdown');
const editAssigneeSearch = document.getElementById('edit-assignee-search');
const editAssigneeOptionList = document.getElementById('edit-assignee-option-list');
const editSelectedAssigneeList = document.getElementById('edit-selected-assignee-list');
const editSelectedAssignees = new Map();

// 서버 렌더링된 기존 담당자로 초기화
if (editSelectedAssigneeList) {
    editSelectedAssigneeList.querySelectorAll('.selected-assignee-chip').forEach(chip => {
        editSelectedAssignees.set(chip.dataset.memberId, chip.dataset.username);
        const opt = editAssigneeOptionList && editAssigneeOptionList.querySelector(`[data-member-id="${chip.dataset.memberId}"]`);
        if (opt) { opt.style.display = 'none'; }
    });
}

// 모달 닫기 시 복원용 원본 저장
const editOriginalAssignees = new Map(editSelectedAssignees);

if (btnEditAddAssignee) {
    btnEditAddAssignee.addEventListener('click', (e) => {
        e.stopPropagation();
        editAssigneeDropdown.classList.toggle('open');
        if (editAssigneeDropdown.classList.contains('open')) {
            editAssigneeSearch.focus();
        }
    });
}

document.addEventListener('click', (e) => {
    if (editAssigneeDropdown && !e.target.closest('#edit-assignee-picker')) {
        editAssigneeDropdown.classList.remove('open');
    }
});

if (editAssigneeSearch) {
    editAssigneeSearch.addEventListener('input', () => {
        const query = editAssigneeSearch.value.trim().toLowerCase();
        editAssigneeOptionList.querySelectorAll('.assignee-option').forEach(opt => {
            if (editSelectedAssignees.has(opt.dataset.memberId)) {
                opt.style.display = 'none';
                return;
            }
            opt.style.display = opt.dataset.username.toLowerCase().includes(query) ? '' : 'none';
        });
    });
}

if (editAssigneeOptionList) {
    editAssigneeOptionList.addEventListener('click', (e) => {
        const opt = e.target.closest('.assignee-option');
        if (!opt) { return; }
        editSelectedAssignees.set(opt.dataset.memberId, opt.dataset.username);
        opt.style.display = 'none';
        renderEditSelectedAssignees();
        editAssigneeSearch.value = '';
        editAssigneeDropdown.classList.remove('open');
    });
}

function renderEditSelectedAssignees() {
    if (!editSelectedAssigneeList) { return; }
    editSelectedAssigneeList.innerHTML = '';
    editSelectedAssignees.forEach((username, memberId) => {
        const chip = document.createElement('span');
        chip.className = 'selected-assignee-chip';
        chip.dataset.memberId = memberId;
        chip.dataset.username = username;
        chip.innerHTML = `<span class="assignee-chip-name">${username}</span><button type="button" class="selected-assignee-remove" data-member-id="${memberId}">×</button>`;
        editSelectedAssigneeList.appendChild(chip);
    });
}

if (editSelectedAssigneeList) {
    editSelectedAssigneeList.addEventListener('click', (e) => {
        const removeBtn = e.target.closest('.selected-assignee-remove');
        if (!removeBtn) { return; }
        const memberId = removeBtn.dataset.memberId;
        editSelectedAssignees.delete(memberId);
        const opt = editAssigneeOptionList && editAssigneeOptionList.querySelector(`[data-member-id="${memberId}"]`);
        if (opt) { opt.style.display = ''; }
        renderEditSelectedAssignees();
    });
}

const editPostForm = document.getElementById('edit-post-form');
if (editPostForm) {
    editPostForm.addEventListener('submit', () => {
        editPostForm.querySelectorAll('input[name="assigneeIds"]').forEach(el => el.remove());
        editSelectedAssignees.forEach((username, memberId) => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'assigneeIds';
            input.value = memberId;
            editPostForm.appendChild(input);
        });
    });
}

// ── 담당자 칩 드롭다운 ──
const btnAssigneeChip = document.getElementById('btn-assignee-chip');
const assigneeChipDropdown = document.getElementById('assignee-chip-dropdown');

if (btnAssigneeChip) {
    btnAssigneeChip.addEventListener('click', (e) => {
        e.stopPropagation();
        assigneeChipDropdown.classList.toggle('open');
    });
}

document.addEventListener('click', (e) => {
    if (assigneeChipDropdown && !e.target.closest('.assignee-chip-wrap')) {
        assigneeChipDropdown.classList.remove('open');
    }
});

// ── 반려 패널 위치 동적 조정 ──
const rejectionPanel = document.querySelector('.rejection-panel');
const btnRejectionToggle = document.getElementById('btn-rejection-toggle');

if (rejectionPanel) {
    const detailWrap = document.querySelector('.detail-wrap');

    function adjustRejectionPanel() {
        if (window.innerWidth <= 768) {
            // 모바일: 인라인 스타일 초기화 → CSS가 제어
            rejectionPanel.style.position = '';
            rejectionPanel.style.top = '';
            rejectionPanel.style.right = '';
            rejectionPanel.style.width = '';
            rejectionPanel.style.marginBottom = '';
            return;
        }
        // 데스크탑: 모바일 토글 초기화
        rejectionPanel.classList.remove('mobile-open');
        if (btnRejectionToggle) { btnRejectionToggle.classList.remove('active'); }

        const spaceRight = window.innerWidth - detailWrap.getBoundingClientRect().right;

        if (spaceRight >= 276) { // 260px 패널 + 16px 여백
            rejectionPanel.style.position     = 'fixed';
            rejectionPanel.style.top          = '140px';
            rejectionPanel.style.right        = '40px';
            rejectionPanel.style.width        = '260px';
            rejectionPanel.style.marginBottom = '';
        } else {
            rejectionPanel.style.position     = 'static';
            rejectionPanel.style.top          = '';
            rejectionPanel.style.right        = '';
            rejectionPanel.style.width        = '100%';
            rejectionPanel.style.marginBottom = '16px';
        }
    }

    window.addEventListener('resize', adjustRejectionPanel);
    adjustRejectionPanel();
}

if (btnRejectionToggle && rejectionPanel) {
    btnRejectionToggle.addEventListener('click', () => {
        const isOpen = rejectionPanel.classList.toggle('mobile-open');
        btnRejectionToggle.classList.toggle('active', isOpen);
    });
}

// ── 답글 ──
(function () {
    var parentIdInput  = document.getElementById('commentParentId');
    var groupIdInput   = document.getElementById('commentGroupId');
    var depthInput     = document.getElementById('commentDepth');
    var replyTarget    = document.getElementById('commentReplyTarget');
    var textarea       = document.getElementById('commentTextarea');

    document.querySelectorAll('.btn-reply').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var commentId = btn.dataset.commentId;
            var groupId   = btn.dataset.groupId;
            var author    = btn.dataset.author;

            parentIdInput.value = commentId;
            groupIdInput.value  = groupId;
            depthInput.value    = '1';

            replyTarget.style.display = 'flex';
            replyTarget.innerHTML =
                '@' + author + ' 에게 답글' +
                ' <button type="button" class="btn-reply-cancel">×</button>';

            replyTarget.querySelector('.btn-reply-cancel').addEventListener('click', function () {
                parentIdInput.value = '';
                groupIdInput.value  = '';
                depthInput.value    = '0';
                replyTarget.style.display = 'none';
                replyTarget.innerHTML = '';
            });

            if (textarea) { textarea.focus(); }
        });
    });
})();

// ── 댓글 작성자 이름 + 글자수 카운터 ──
(function () {
    var currentMemberId = parseInt(document.body.dataset.currentMemberId);
    var authorName      = document.getElementById('commentAuthorName');
    var charCount       = document.getElementById('commentCharCount');
    var textarea        = document.getElementById('commentTextarea');
    var MAX             = 256;

    if (authorName && currentMemberId) {
        fetchUsername(currentMemberId).then(function (name) {
            authorName.textContent = name;
        });
    }

    if (textarea && charCount) {
        textarea.addEventListener('input', function () {
            var len = this.value.length;
            charCount.textContent = len + '/' + MAX;
            charCount.classList.toggle('near-limit', len >= MAX - 16);

            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    }
})();

// ── 댓글 수정 ──
(function () {
    var boardId = document.body.dataset.boardId;
    var postId  = document.body.dataset.postId;

    document.querySelectorAll('.btn-comment-edit').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var item       = btn.closest('.comment-item');
            var contentDiv = item.querySelector('.comment-content');
            var commentId  = item.dataset.commentId;
            var currentText = item.querySelector('.comment-content span:last-child').textContent;

            contentDiv.style.display = 'none';

            var editArea = document.createElement('div');
            editArea.className = 'comment-edit-area';
            editArea.innerHTML =
                '<textarea class="comment-edit-textarea">' + currentText + '</textarea>' +
                '<div class="comment-edit-actions">' +
                    '<button type="button" class="btn-comment-edit-cancel">취소</button>' +
                    '<button type="button" class="btn-comment-edit-save">저장</button>' +
                '</div>';
            contentDiv.insertAdjacentElement('afterend', editArea);

            editArea.querySelector('.btn-comment-edit-cancel').addEventListener('click', function () {
                contentDiv.style.display = '';
                editArea.remove();
            });

            editArea.querySelector('.btn-comment-edit-save').addEventListener('click', function () {
                var content = editArea.querySelector('.comment-edit-textarea').value.trim();
                if (!content) { return; }

                var form = document.createElement('form');
                form.method = 'POST';
                form.action = '/board/' + boardId + '/post/' + postId + '/comment/' + commentId + '/edit';

                var input = document.createElement('input');
                input.type  = 'hidden';
                input.name  = 'content';
                input.value = content;
                form.appendChild(input);

                document.body.appendChild(form);
                form.submit();
            });
        });
    });
})();
