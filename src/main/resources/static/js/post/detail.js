// ── 수정 모달 ──
const btnEdit = document.getElementById("btn-edit");
const btnEditClose = document.getElementById("btn-edit-close");

function closeEditModal() {
    closeModal("editModal");
    if (editFileManager) { editFileManager.reset(); }
    if (editAssigneePicker) { editAssigneePicker.reset(); }
}

const editTitleInput = document.getElementById("edit-title");
const editTitleCount = document.getElementById("editTitleCount");
initCharCounter(editTitleInput, editTitleCount, 100, 90);

if (btnEdit) {
    btnEdit.addEventListener("click", () => {
        openModal("editModal");
    });
}
if (btnEditClose) {
    btnEditClose.addEventListener("click", closeEditModal);
}

// ── 반려 모달 ──
const btnReject = document.querySelector(".btn-reject");
const boardId = document.body.dataset.boardId;

const rejectReasonTextarea = document.getElementById("rejectReason");
const rejectReasonCount = document.getElementById("rejectReasonCount");
initCharCounter(rejectReasonTextarea, rejectReasonCount, 300, 270);

if (btnReject) {
    btnReject.addEventListener("click", () => {
        document.getElementById("rejectForm").action =
            "/board/" + boardId + "/manager/requests/reject/" + btnReject.dataset.postId;
        openModal("rejectModal");
    });
}

// ── 삭제 모달 ──
const btnDelete = document.getElementById("btn-delete");
const btnDeleteCancel = document.getElementById("btn-delete-cancel");

if (btnDelete) {
    btnDelete.addEventListener("click", () => {
        openModal("deleteModal");
    });
}
if (btnDeleteCancel) {
    btnDeleteCancel.addEventListener("click", () => {
        closeModal("deleteModal");
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

// ── 수정 모달 캘린더 피커 ──
initDatePicker('edit', { initialDate: existingDueDateStr });

// ── 수정 모달 파일 첨부 ──
const editFileManager = initFileAttachment('edit-file-input', 'btn-edit-file-attach', 'edit-file-chip-list');

// ── 수정 모달 담당자 선택 ──
const editAssigneePicker = initAssigneePicker({
    addBtnId:      'btn-edit-add-assignee',
    dropdownId:    'edit-assignee-dropdown',
    searchId:      'edit-assignee-search',
    optionListId:  'edit-assignee-option-list',
    selectedListId:'edit-selected-assignee-list',
    pickerId:      'edit-assignee-picker',
    formId:        'edit-post-form',
});

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
