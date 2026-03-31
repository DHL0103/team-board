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
        PostDetailPage.#editAssigneePicker = new AssigneePicker({
            addBtnId:      'btn-edit-add-assignee',
            dropdownId:    'edit-assignee-dropdown',
            searchId:      'edit-assignee-search',
            optionListId:  'edit-assignee-option-list',
            selectedListId:'edit-selected-assignee-list',
            pickerId:      'edit-assignee-picker',
            formId:        'edit-post-form',
        });
    }

    static #closeEditModal() {
        Modal.close('editModal');
        if (PostDetailPage.#editFileAttachment) { PostDetailPage.#editFileAttachment.reset(); }
        if (PostDetailPage.#editAssigneePicker) { PostDetailPage.#editAssigneePicker.reset(); }
    }

    static #initRejectModal() {
        const btnReject = document.querySelector('.btn-reject');
        const boardId   = document.body.dataset.boardId;

        new CharCounter(document.getElementById('rejectReason'), document.getElementById('rejectReasonCount'), 300, 270);

        if (btnReject) {
            btnReject.addEventListener('click', () => {
                document.getElementById('rejectForm').action =
                    '/board/' + boardId + '/manager/requests/reject/' + btnReject.dataset.postId;
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

        const detailWrap = document.querySelector('.detail-wrap');

        function adjustRejectionPanel() {
            if (window.innerWidth <= 768) {
                rejectionPanel.style.position = '';
                rejectionPanel.style.top      = '';
                rejectionPanel.style.right    = '';
                rejectionPanel.style.width    = '';
                rejectionPanel.style.marginBottom = '';
                return;
            }
            rejectionPanel.classList.remove('mobile-open');
            if (btnRejectionToggle) { btnRejectionToggle.classList.remove('active'); }

            const spaceRight = window.innerWidth - detailWrap.getBoundingClientRect().right;
            if (spaceRight >= 276) {
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

                const editArea = document.createElement('div');
                editArea.className = 'comment-edit-area';
                editArea.innerHTML = '<textarea class="comment-edit-textarea">' + currentText + '</textarea>'
                    + '<div class="comment-edit-actions">'
                    + '<button type="button" class="btn-comment-edit-cancel">취소</button>'
                    + '<button type="button" class="btn-comment-edit-save">저장</button>'
                    + '</div>';
                contentDiv.insertAdjacentElement('afterend', editArea);

                editArea.querySelector('.btn-comment-edit-cancel').addEventListener('click', () => {
                    contentDiv.style.display = '';
                    editArea.remove();
                });

                editArea.querySelector('.btn-comment-edit-save').addEventListener('click', () => {
                    const content = editArea.querySelector('.comment-edit-textarea').value.trim();
                    if (!content) { return; }
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '/board/' + boardId + '/post/' + postId + '/comment/' + commentId + '/edit';
                    const input = document.createElement('input');
                    input.type  = 'hidden';
                    input.name  = 'content';
                    input.value = content;
                    form.appendChild(input);
                    document.body.appendChild(form);
                    form.submit();
                });
            });
        });
    }


}

document.addEventListener('DOMContentLoaded', () => PostDetailPage.init());
