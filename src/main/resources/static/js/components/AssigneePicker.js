class AssigneePicker {

    #addBtn;
    #dropdown;
    #search;
    #optionList;
    #selectedList;
    #form;
    #inputName;
    #selected = new Map();
    #original = new Map();

    constructor({ addBtnId, dropdownId, searchId, optionListId, selectedListId, pickerId, formId, inputName = 'assigneeIds' }) {
        this.#addBtn       = document.getElementById(addBtnId);
        this.#dropdown     = document.getElementById(dropdownId);
        this.#search       = searchId ? document.getElementById(searchId) : null;
        this.#optionList   = document.getElementById(optionListId);
        this.#selectedList = document.getElementById(selectedListId);
        this.#form         = formId ? document.getElementById(formId) : null;
        this.#inputName    = inputName;

        if (!this.#addBtn || !this.#dropdown || !this.#optionList || !this.#selectedList) { return; }

        this.#selectedList.querySelectorAll('.selected-assignee-chip').forEach(chip => {
            this.#selected.set(chip.dataset.memberId, chip.dataset.username);
            const opt = this.#optionList.querySelector(`[data-member-id="${chip.dataset.memberId}"]`);
            if (opt) { opt.style.display = 'none'; }
        });
        this.#original = new Map(this.#selected);

        this.#addBtn.addEventListener('click', e => {
            e.stopPropagation();
            this.#dropdown.classList.toggle('open');
            if (this.#dropdown.classList.contains('open') && this.#search) { this.#search.focus(); }
        });

        if (pickerId) {
            document.addEventListener('click', e => {
                if (!e.target.closest('#' + pickerId)) { this.#dropdown.classList.remove('open'); }
            });
        }

        if (this.#search) {
            this.#search.addEventListener('input', () => this.#filterOptions());
        }

        this.#optionList.addEventListener('click', e => this.#onSelect(e));
        this.#selectedList.addEventListener('click', e => this.#onRemove(e));

        if (this.#form) {
            this.#form.addEventListener('submit', () => this.#injectInputs());
        }
    }

    #filterOptions() {
        const query = this.#search.value.trim().toLowerCase();
        this.#optionList.querySelectorAll('.assignee-option').forEach(opt => {
            if (this.#selected.has(opt.dataset.memberId)) { opt.style.display = 'none'; return; }
            opt.style.display = opt.dataset.username.toLowerCase().includes(query) ? '' : 'none';
        });
    }

    #onSelect(e) {
        const opt = e.target.closest('.assignee-option');
        if (!opt) { return; }
        this.#selected.set(opt.dataset.memberId, opt.dataset.username);
        opt.style.display = 'none';
        this.#renderChips();
        if (this.#search) { this.#search.value = ''; }
        this.#dropdown.classList.remove('open');
    }

    #onRemove(e) {
        const removeBtn = e.target.closest('.selected-assignee-remove');
        if (!removeBtn) { return; }
        const memberId = removeBtn.dataset.memberId;
        this.#selected.delete(memberId);
        const opt = this.#optionList.querySelector(`[data-member-id="${memberId}"]`);
        if (opt) { opt.style.display = ''; }
        this.#renderChips();
    }

    #renderChips() {
        this.#selectedList.innerHTML = '';
        this.#selected.forEach((username, memberId) => {
            const chip = document.createElement('span');
            chip.className = 'selected-assignee-chip';
            chip.dataset.memberId = memberId;
            chip.dataset.username = username;
            chip.innerHTML = `<span class="assignee-chip-name">${username}</span>`
                + `<button type="button" class="selected-assignee-remove" data-member-id="${memberId}">×</button>`;
            this.#selectedList.appendChild(chip);
        });
    }

    #injectInputs() {
        this.#form.querySelectorAll(`input[name="${this.#inputName}"]`).forEach(el => el.remove());
        this.#selected.forEach((username, memberId) => {
            const input = document.createElement('input');
            input.type  = 'hidden';
            input.name  = this.#inputName;
            input.value = memberId;
            this.#form.appendChild(input);
        });
    }

    reset() {
        this.#selected.clear();
        this.#original.forEach((username, memberId) => this.#selected.set(memberId, username));
        this.#renderChips();
        this.#optionList.querySelectorAll('.assignee-option').forEach(opt => {
            opt.style.display = this.#selected.has(opt.dataset.memberId) ? 'none' : '';
        });
        if (this.#search) { this.#search.value = ''; }
        this.#dropdown.classList.remove('open');
    }
}
