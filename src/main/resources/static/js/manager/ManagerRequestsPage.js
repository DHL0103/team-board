class ManagerRequestsPage {

    static #initialized = false;

    static init() {
        if (ManagerRequestsPage.#initialized) { return; }
        ManagerRequestsPage.#initialized = true;

        ManagerRequestsPage.#bindRejectModal();
    }

    static #bindRejectModal() {
        const boardId = document.body.dataset.boardId;

        document.querySelectorAll('.btn-reject').forEach(btn => {
            btn.addEventListener('click', () => {
                document.getElementById('rejectForm').action =
                    '/board/' + boardId + '/manager/requests/reject/' + btn.dataset.postId;
                Modal.open('rejectModal');
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => ManagerRequestsPage.init());
