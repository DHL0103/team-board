class AdminPage {

    static #initialized = false;

    static init() {
        if (AdminPage.#initialized) { return; }
        AdminPage.#initialized = true;

        Modal.init();
        AdminPage.#bindRejectModal();
        AdminPage.#bindCreateModal();
        AdminPage.#bindEditModal();
        AdminPage.#bindDeleteMemberModal();
    }

    static #bindRejectModal() {
        document.querySelectorAll('.btn-reject').forEach(btn => {
            btn.addEventListener('click', () => {
                document.getElementById('rejectForm').action = '/admin/reject/' + btn.dataset.postId;
                Modal.open('rejectModal');
            });
        });
    }

    static #bindCreateModal() {
        const btnOpenCreate = document.getElementById('btn-open-create');
        if (btnOpenCreate) {
            btnOpenCreate.addEventListener('click', () => {
                if (document.getElementById('createMemberModal')) { Modal.open('createMemberModal'); }
                else if (document.getElementById('createBoardModal')) { Modal.open('createBoardModal'); }
            });
        }

        const btnCloseCreate = document.getElementById('btn-close-create');
        if (btnCloseCreate) {
            btnCloseCreate.addEventListener('click', () => {
                if (document.getElementById('createMemberModal')) { Modal.close('createMemberModal'); }
                else if (document.getElementById('createBoardModal')) { Modal.close('createBoardModal'); }
            });
        }
    }

    static #bindEditModal() {
        const btnCloseEdit = document.getElementById('btn-close-edit');
        if (btnCloseEdit) {
            btnCloseEdit.addEventListener('click', () => {
                if (document.getElementById('editMemberModal')) { Modal.close('editMemberModal'); }
                else if (document.getElementById('editBoardModal')) { Modal.close('editBoardModal'); }
            });
        }

        document.querySelectorAll('.btn-open-edit').forEach(btn => {
            btn.addEventListener('click', () => {
                const editMemberForm = document.getElementById('editMemberForm');
                if (editMemberForm) {
                    editMemberForm.action = `/admin/member/update/${btn.dataset.memberId}`;
                    const roleSelect = document.getElementById('edit-role-select');
                    if (roleSelect) { roleSelect.value = btn.dataset.role ?? 'ROLE_USER'; }
                    const select = document.getElementById('edit-board-select');
                    if (select) { select.value = btn.dataset.boardId ?? ''; }
                    Modal.open('editMemberModal');
                    return;
                }

                const editBoardForm = document.getElementById('editBoardForm');
                if (editBoardForm) {
                    editBoardForm.action = `/admin/board/update/${btn.dataset.boardId}`;
                    document.getElementById('edit-board-name').value = btn.dataset.boardName ?? '';
                    document.getElementById('edit-board-slug').value = btn.dataset.boardSlug ?? '';
                    document.getElementById('edit-board-desc').value = btn.dataset.boardDesc ?? '';
                    Modal.open('editBoardModal');
                }
            });
        });
    }

    static #bindDeleteMemberModal() {
        document.querySelectorAll('.btn-open-delete-member').forEach(btn => {
            btn.addEventListener('click', () => {
                document.getElementById('delete-member-name').textContent = btn.dataset.username;
                document.getElementById('deleteMemberForm').action = `/admin/member/delete/${btn.dataset.memberId}`;
                Modal.open('deleteMemberModal');
            });
        });

        const btnCancel = document.getElementById('btn-delete-member-cancel');
        if (btnCancel) {
            btnCancel.addEventListener('click', () => Modal.close('deleteMemberModal'));
        }
    }
}

document.addEventListener('DOMContentLoaded', () => AdminPage.init());
