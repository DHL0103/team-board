class BoardDetailPage {

    static #initialized = false;
    static #createFileAttachment;
    static #createAssigneePicker;

    static init() {
        if (BoardDetailPage.#initialized) { return; }
        BoardDetailPage.#initialized = true;

        DueChip.init();
        Modal.init();
        BoardDetailPage.#bindCardLinks();
        BoardDetailPage.#initCreateModal();
    }

    static #bindCardLinks() {
        document.querySelectorAll('.board-card[data-href]').forEach(card => {
            card.addEventListener('click', () => {
                window.location.href = card.dataset.href;
            });
        });
    }

    static #initCreateModal() {
        const btnCreate = document.getElementById('btn-create-post');
        const btnClose  = document.getElementById('btn-create-close');

        btnCreate.addEventListener('click', () => Modal.open('createModal'));
        btnClose.addEventListener('click', () => BoardDetailPage.#closeCreateModal());

        new DatePicker('create', { formatDisplay: (m, d) => `${parseInt(m)}/${parseInt(d)}` });
        BoardDetailPage.#createFileAttachment = new FileAttachment('create-file-input', 'btn-create-file-attach', 'create-file-chip-list');
        BoardDetailPage.#createAssigneePicker = new AssigneePicker({
            addBtnId:      'btn-add-assignee',
            dropdownId:    'assignee-dropdown',
            searchId:      'create-assignee-search',
            optionListId:  'assignee-option-list',
            selectedListId:'selected-assignee-list',
            pickerId:      'assignee-picker',
            formId:        'create-post-form',
        });
        new CharCounter(document.getElementById('create-title'), document.getElementById('createTitleCount'), 100, 90);
    }

    static #closeCreateModal() {
        Modal.close('createModal');
        BoardDetailPage.#createFileAttachment.reset();
        BoardDetailPage.#createAssigneePicker.reset();
    }
}

document.addEventListener('DOMContentLoaded', () => BoardDetailPage.init());
