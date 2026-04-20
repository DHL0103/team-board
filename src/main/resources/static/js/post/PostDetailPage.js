class PostDetailPage {

    static #initialized = false;
    static #editFileAttachment;
    static #editAssigneePicker;

    static init() {
        if (PostDetailPage.#initialized) { return; }
        PostDetailPage.#initialized = true;

        PostDetailPage.#initEditModal();
        PostDetailPage.#initRejectModal();
        PostDetailPage.#initDeleteModal();
        PostDetailPage.#initAssigneeChipDropdown();
        PostDetailPage.#initRejectionPanel();
        PostDetailPage.#initReply();
        PostDetailPage.#initCommentSection();
        PostDetailPage.#initCommentEdit();
        PostDetailPage.#initRejectionReasonToggle();
    }

    static #initEditModal() {
        const btnEdit      = document.getElementById('btn-edit');
        const btnEditClose = document.getElementById('btn-edit-close');

        new CharCounter(document.getElementById('edit-title'), document.getElementById('editTitleCount'), 100, 90);

        if (btnEdit) { btnEdit.addEventListener('click', () => Modal.open('editModal')); }
        if (btnEditClose) { btnEditClose.addEventListener('click', () => PostDetailPage.#closeEditModal()); }

        const existingDueDateStr = document.body.dataset.dueDate || '';
        new DatePicker('edit', { initialDate: existingDueDateStr });
        PostDetailPage.#editFileAttachment = new FileAttachment('edit-file-input', 'btn-edit-file-attach', 'edit-file-chip-list');
        PostDetailPage.#editFileAttachment.setExistingSizeFn(() => {
            let size = 0;
            document.querySelectorAll('#edit-file-stack .existing-file-item:not(.will-delete)').forEach(r => {
                size += parseInt(r.dataset.size || '0');
            });
            return size;
        });
        PostDetailPage.#editAssigneePicker = new AssigneePicker({
            addBtnId:      'btn-edit-add-assignee',
            dropdownId:    'edit-assignee-dropdown',
            searchId:      'edit-assignee-search',
            optionListId:  'edit-assignee-option-list',
            selectedListId:'edit-selected-assignee-list',
            pickerId:      'edit-assignee-picker',
            formId:        'edit-post-form',
        });

        // -- Existing file checkbox toggle UI --
        document.querySelectorAll('#edit-file-stack .existing-file-item').forEach(row => {
            const cb = row.querySelector('input[name="deleteFileIds"]');
            if (!cb) { return; }
            row.addEventListener('click', e => {
                if (e.target.tagName === 'INPUT') { return; }
                cb.checked = !cb.checked;
                cb.dispatchEvent(new Event('change', { bubbles: true }));
            });
            const sizeMB = (parseInt(row.dataset.size || '0') / (1024 * 1024)).toFixed(1);
            cb.addEventListener('change', () => {
                row.classList.toggle('will-delete', cb.checked);
                const state = row.querySelector('.state');
                state.className = 'state ' + (cb.checked ? 'del' : 'keep');
                state.textContent = cb.checked ? '삭제' : '유지';
                const sub = row.querySelector('.sub');
                if (sub) {
                    sub.textContent = cb.checked
                        ? '저장 시 삭제됨 · ' + sizeMB + 'MB · 다시 클릭 시 되돌리기'
                        : '기존 파일 · ' + sizeMB + 'MB · 클릭 시 삭제 표시';
                }
                window.updateFileHint();
            });
        });

        // -- File count/hint update --
        const fa = PostDetailPage.#editFileAttachment;
        window.updateFileHint = function() {
            const stack = document.getElementById('edit-file-stack');
            if (!stack) { return; }
            const keepRows = stack.querySelectorAll('.existing-file-item:not(.will-delete)');
            const delRows  = stack.querySelectorAll('.existing-file-item.will-delete');
            const newCount = fa ? fa.newFilesCount : 0;
            const total    = keepRows.length + delRows.length + newCount;

            let keepSize = 0;
            keepRows.forEach(r => { keepSize += parseInt(r.dataset.size || '0'); });
            const newSize   = fa ? fa.newFilesTotalSize : 0;
            const totalSize = keepSize + newSize;
            const limitBytes = 50 * 1024 * 1024;

            const countEl = document.getElementById('edit-file-count');
            const hintEl  = document.getElementById('edit-file-hint');
            if (countEl) { countEl.textContent = total; }
            if (hintEl) {
                const sizeMB  = (totalSize / (1024 * 1024)).toFixed(1);
                const limitMB = '50';
                hintEl.textContent = sizeMB + ' / ' + limitMB + 'MB';
                hintEl.classList.toggle('over-limit', totalSize > limitBytes);
            }
        };
        window.updateFileHint();
    }

    static #closeEditModal() {
        Modal.close('editModal');
        if (PostDetailPage.#editFileAttachment) { PostDetailPage.#editFileAttachment.reset(); }
        if (PostDetailPage.#editAssigneePicker) { PostDetailPage.#editAssigneePicker.reset(); }
        // Reset existing file toggle state
        document.querySelectorAll('#edit-file-stack .existing-file-item').forEach(row => {
            const cb = row.querySelector('input[name="deleteFileIds"]');
            if (cb) { cb.checked = false; }
            row.classList.remove('will-delete');
            const state = row.querySelector('.state');
            if (state) { state.className = 'state keep'; state.textContent = '유지'; }
            const sub = row.querySelector('.sub');
            if (sub) {
                const sizeMB = (parseInt(row.dataset.size || '0') / (1024 * 1024)).toFixed(1);
                sub.textContent = '기존 파일 · ' + sizeMB + 'MB · 클릭 시 삭제 표시';
            }
        });
        if (typeof window.updateFileHint === 'function') { window.updateFileHint(); }
    }

    static #initRejectModal() {
        const btnReject = document.querySelector('.btn-reject');
        const boardId   = document.body.dataset.boardId;

        new CharCounter(document.getElementById('rejectReason'), document.getElementById('rejectReasonCount'), 300, 270);

        if (btnReject) {
            btnReject.addEventListener('click', () => {
                document.getElementById('rejectForm').action =
                    '/boards/' + boardId + '/manager/requests/reject/' + btnReject.dataset.postId;
                Modal.open('rejectModal');
            });
        }
    }

    static #initDeleteModal() {
        const btnDelete       = document.getElementById('btn-delete');
        const btnDeleteCancel = document.getElementById('btn-delete-cancel');

        if (btnDelete) { btnDelete.addEventListener('click', () => Modal.open('deleteModal')); }
        if (btnDeleteCancel) { btnDeleteCancel.addEventListener('click', () => Modal.close('deleteModal')); }
    }

    static #initAssigneeChipDropdown() {
        const btnChip    = document.getElementById('btn-assignee-chip');
        const dropdown   = document.getElementById('assignee-chip-dropdown');
        if (!btnChip) { return; }

        btnChip.addEventListener('click', e => {
            e.stopPropagation();
            dropdown.classList.toggle('open');
        });
        document.addEventListener('click', e => {
            if (dropdown && !e.target.closest('.assignee-chip-wrap')) {
                dropdown.classList.remove('open');
            }
        });
    }

    static #initRejectionPanel() {
        const rejectionPanel    = document.querySelector('.rejection-panel');
        const btnRejectionToggle = document.getElementById('btn-rejection-toggle');
        if (!rejectionPanel) { return; }

        function onResize() {
            if (window.innerWidth >= 1240) {
                rejectionPanel.classList.remove('mobile-open');
                if (btnRejectionToggle) { btnRejectionToggle.classList.remove('active'); }
            }
        }

        window.addEventListener('resize', onResize);

        if (btnRejectionToggle) {
            btnRejectionToggle.addEventListener('click', () => {
                const isOpen = rejectionPanel.classList.toggle('mobile-open');
                btnRejectionToggle.classList.toggle('active', isOpen);
            });
        }
    }

    static #initRejectionReasonToggle() {
        document.querySelectorAll('.rejection-reason').forEach(el => {
            const fullHeight = el.scrollHeight;
            el.classList.add('clamped');
            if (fullHeight > el.clientHeight) {
                const btn = document.createElement('button');
                btn.className   = 'rejection-reason-toggle';
                btn.textContent = '더보기';
                btn.addEventListener('click', () => {
                    const expanded = el.classList.toggle('expanded');
                    el.classList.toggle('clamped', !expanded);
                    btn.textContent = expanded ? '접기' : '더보기';
                });
                el.insertAdjacentElement('afterend', btn);
            }
        });
    }

    static #initReply() {
        const parentIdInput = document.getElementById('commentParentId');
        const groupIdInput  = document.getElementById('commentGroupId');
        const depthInput    = document.getElementById('commentDepth');
        const replyTarget   = document.getElementById('commentReplyTarget');
        const textarea      = document.getElementById('commentTextarea');

        document.querySelectorAll('.btn-reply').forEach(btn => {
            btn.addEventListener('click', () => {
                parentIdInput.value = btn.dataset.commentId;
                groupIdInput.value  = btn.dataset.groupId;
                depthInput.value    = '1';

                replyTarget.style.display = 'flex';
                replyTarget.innerHTML = '@' + btn.dataset.author + ' 에게 답글'
                    + ' <button type="button" class="btn-reply-cancel">×</button>';

                replyTarget.querySelector('.btn-reply-cancel').addEventListener('click', () => {
                    parentIdInput.value = '';
                    groupIdInput.value  = '';
                    depthInput.value    = '0';
                    replyTarget.style.display = 'none';
                    replyTarget.innerHTML = '';
                });

                if (textarea) { textarea.focus(); }
            });
        });
    }

    static #initCommentSection() {
        const charCount = document.getElementById('commentCharCount');
        const textarea  = document.getElementById('commentTextarea');
        const MAX       = 256;

        if (textarea && charCount) {
            textarea.addEventListener('input', function () {
                const len = this.value.length;
                charCount.textContent = len + '/' + MAX;
                charCount.classList.toggle('near-limit', len >= MAX - 16);
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
        }
    }

    static #initCommentEdit() {
        const boardId = document.body.dataset.boardId;
        const postId  = document.body.dataset.postId;

        document.querySelectorAll('.btn-comment-edit').forEach(btn => {
            btn.addEventListener('click', () => {
                const item        = btn.closest('.comment-item');
                const contentDiv  = item.querySelector('.comment-content');
                const commentId   = item.dataset.commentId;
                const currentText = item.querySelector('.comment-content span:last-child').textContent;

                contentDiv.style.display = 'none';

                const MAX = 256;
                const editArea = document.createElement('div');
                editArea.className = 'comment-edit-area';
                editArea.innerHTML = '<div class="comment-edit-header">'
                    + '<span class="comment-char-count">' + currentText.length + '/' + MAX + '</span>'
                    + '</div>'
                    + '<textarea class="comment-edit-textarea" maxlength="256">' + currentText + '</textarea>'
                    + '<div class="comment-edit-actions">'
                    + '<button type="button" class="btn-comment-edit-cancel">취소</button>'
                    + '<button type="button" class="btn-comment-edit-save">저장</button>'
                    + '</div>';
                contentDiv.insertAdjacentElement('afterend', editArea);

                const editTextarea = editArea.querySelector('.comment-edit-textarea');
                const editCharCount = editArea.querySelector('.comment-char-count');
                editCharCount.classList.toggle('near-limit', currentText.length >= MAX - 16);

                editTextarea.addEventListener('input', function () {
                    const len = this.value.length;
                    editCharCount.textContent = len + '/' + MAX;
                    editCharCount.classList.toggle('near-limit', len >= MAX - 16);
                });

                editArea.querySelector('.btn-comment-edit-cancel').addEventListener('click', () => {
                    contentDiv.style.display = '';
                    editArea.remove();
                });

                editArea.querySelector('.btn-comment-edit-save').addEventListener('click', () => {
                    const content = editArea.querySelector('.comment-edit-textarea').value.trim();
                    if (!content || content.length > MAX) { return; }
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '/boards/' + boardId + '/posts/' + postId + '/comments/' + commentId + '/edit';
                    const input = document.createElement('input');
                    input.type  = 'hidden';
                    input.name  = 'content';
                    input.value = content;
                    form.appendChild(input);
                    const csrfMeta   = document.querySelector('meta[name="_csrf"]');
                    const csrfParam  = document.querySelector('meta[name="_csrf_param"]');
                    if (csrfMeta && csrfParam) {
                        const csrfInput   = document.createElement('input');
                        csrfInput.type    = 'hidden';
                        csrfInput.name    = csrfParam.content;
                        csrfInput.value   = csrfMeta.content;
                        form.appendChild(csrfInput);
                    }
                    document.body.appendChild(form);
                    form.submit();
                });
            });
        });
    }


}

document.addEventListener('DOMContentLoaded', () => PostDetailPage.init());
