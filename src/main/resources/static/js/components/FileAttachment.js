class FileAttachment {

    #fileInput;
    #chipList;
    #counter;
    #maxSize;
    #dataTransfer = new DataTransfer();

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

    #onFileChange(maxSizeMB) {
        for (const file of this.#fileInput.files) {
            if (file.size > this.#maxSize) {
                Toast.show(file.name + ': 파일 크기는 ' + (maxSizeMB ?? 10) + 'MB를 초과할 수 없습니다.');
                continue;
            }
            let currentTotal = 0;
            for (let i = 0; i < this.#dataTransfer.files.length; i++) {
                currentTotal += this.#dataTransfer.files[i].size;
            }
            if (currentTotal + file.size > FileAttachment.#TOTAL_MAX_SIZE) {
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
            const chip = document.createElement('span');
            chip.className = 'file-chip';
            chip.innerHTML = file.name + '<button type="button" class="file-chip-remove" data-index="' + i + '">×</button>';
            this.#chipList.appendChild(chip);
        }
        const count = this.#dataTransfer.files.length;
        if (count > 0) {
            const sizeMB = (totalSize / (1024 * 1024)).toFixed(1);
            this.#counter.textContent = count + '개 첨부 · ' + sizeMB + 'MB / 50MB';
            this.#counter.style.display = '';
        } else {
            this.#counter.style.display = 'none';
        }
    }

    reset() {
        this.#dataTransfer = new DataTransfer();
        this.#fileInput.files = this.#dataTransfer.files;
        this.#chipList.innerHTML = '';
        this.#counter.style.display = 'none';
    }
}
