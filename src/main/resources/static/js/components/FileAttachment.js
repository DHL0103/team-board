class FileAttachment {

    #fileInput;
    #chipList;
    #counter;
    #maxSize;
    #dataTransfer = new DataTransfer();
    #existingSizeFn = null;

    static #TOTAL_MAX_SIZE = 50 * 1024 * 1024;

    constructor(fileInputId, attachBtnId, chipListId, maxSizeMB) {
        this.#fileInput = document.getElementById(fileInputId);
        this.#chipList  = document.getElementById(chipListId);
        const attachBtn = document.getElementById(attachBtnId);
        if (!this.#fileInput || !this.#chipList) { return; }

        this.#maxSize = (maxSizeMB ?? 10) * 1024 * 1024;

        this.#counter = document.createElement('div');
        this.#counter.className = 'file-attach-counter';
        this.#counter.style.display = 'none';
        this.#chipList.insertAdjacentElement('afterend', this.#counter);

        if (attachBtn) {
            attachBtn.addEventListener('click', () => this.#fileInput.click());
        }

        this.#fileInput.addEventListener('change', () => this.#onFileChange(maxSizeMB));
        this.#chipList.addEventListener('click', e => this.#onChipRemove(e));
    }

    setExistingSizeFn(fn) {
        this.#existingSizeFn = fn;
    }

    #onFileChange(maxSizeMB) {
        for (const file of this.#fileInput.files) {
            if (file.size > this.#maxSize) {
                Toast.show(file.name + ': 파일 크기는 ' + (maxSizeMB ?? 10) + 'MB를 초과할 수 없습니다.');
                continue;
            }
            let newTotal = 0;
            for (let i = 0; i < this.#dataTransfer.files.length; i++) {
                newTotal += this.#dataTransfer.files[i].size;
            }
            const existingSize = this.#existingSizeFn ? this.#existingSizeFn() : 0;
            if (existingSize + newTotal + file.size > FileAttachment.#TOTAL_MAX_SIZE) {
                Toast.show('총 첨부 용량은 50MB를 초과할 수 없습니다.');
                continue;
            }
            this.#dataTransfer.items.add(file);
        }
        this.#fileInput.files = this.#dataTransfer.files;
        this.#renderChips();
    }

    #onChipRemove(e) {
        if (!e.target.classList.contains('file-chip-remove')) { return; }
        const idx   = parseInt(e.target.dataset.index);
        const newDt = new DataTransfer();
        for (let i = 0; i < this.#dataTransfer.files.length; i++) {
            if (i !== idx) { newDt.items.add(this.#dataTransfer.files[i]); }
        }
        this.#dataTransfer = newDt;
        this.#fileInput.files = this.#dataTransfer.files;
        this.#renderChips();
    }

    #renderChips() {
        this.#chipList.innerHTML = '';
        let totalSize = 0;
        for (let i = 0; i < this.#dataTransfer.files.length; i++) {
            const file = this.#dataTransfer.files[i];
            totalSize += file.size;
            const row = document.createElement('div');
            row.className = 'file-row new';
            row.innerHTML = '<span class="ic"><svg width="14" height="14"><use href="/img/icons.svg#icon-file"></use></svg></span>'
                + '<span class="meta">'
                + '<span class="fn">' + file.name + '</span>'
                + '<span class="sub">새 파일 · ' + (file.size / (1024 * 1024)).toFixed(1) + 'MB</span>'
                + '</span>'
                + '<span class="state new">추가</span>'
                + '<button type="button" class="x file-chip-remove" data-index="' + i + '">×</button>';
            this.#chipList.appendChild(row);
        }
        this.#counter.style.display = 'none';
        if (typeof window.updateFileHint === 'function') { window.updateFileHint(); }
    }

    get newFilesTotalSize() {
        let total = 0;
        for (let i = 0; i < this.#dataTransfer.files.length; i++) {
            total += this.#dataTransfer.files[i].size;
        }
        return total;
    }

    get newFilesCount() {
        return this.#dataTransfer.files.length;
    }

    reset() {
        this.#dataTransfer = new DataTransfer();
        this.#fileInput.files = this.#dataTransfer.files;
        this.#chipList.innerHTML = '';
        this.#counter.style.display = 'none';
    }
}
