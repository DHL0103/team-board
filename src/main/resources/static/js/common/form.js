// ── 파일 첨부 ──
App.initFileAttachment = function (fileInputId, attachBtnId, chipListId, maxSizeMB, maxCount) {
    const fileInput = document.getElementById(fileInputId);
    const chipList  = document.getElementById(chipListId);
    const attachBtn = document.getElementById(attachBtnId);
    if (!fileInput || !chipList) { return; }

    const maxSize  = (maxSizeMB ?? 10) * 1024 * 1024;
    const maxFiles = maxCount ?? 5;
    let dataTransfer = new DataTransfer();

    if (attachBtn) {
        attachBtn.addEventListener('click', function () { fileInput.click(); });
    }

    fileInput.addEventListener('change', function () {
        for (const file of fileInput.files) {
            if (dataTransfer.files.length >= maxFiles) {
                App.showToast('파일은 최대 ' + maxFiles + '개까지 첨부할 수 있습니다.');
                break;
            }
            if (file.size > maxSize) {
                App.showToast(file.name + ': 파일 크기는 ' + (maxSizeMB ?? 10) + 'MB를 초과할 수 없습니다.');
                continue;
            }
            dataTransfer.items.add(file);
        }
        fileInput.files = dataTransfer.files;
        renderChips();
    });

    chipList.addEventListener('click', function (e) {
        if (!e.target.classList.contains('file-chip-remove')) { return; }
        const idx  = parseInt(e.target.dataset.index);
        const newDt = new DataTransfer();
        for (let i = 0; i < dataTransfer.files.length; i++) {
            if (i !== idx) { newDt.items.add(dataTransfer.files[i]); }
        }
        dataTransfer = newDt;
        fileInput.files = dataTransfer.files;
        renderChips();
    });

    function renderChips() {
        chipList.innerHTML = '';
        for (let i = 0; i < dataTransfer.files.length; i++) {
            const file = dataTransfer.files[i];
            const chip = document.createElement('span');
            chip.className = 'file-chip';
            chip.innerHTML = file.name + '<button type="button" class="file-chip-remove" data-index="' + i + '">×</button>';
            chipList.appendChild(chip);
        }
    }

    return {
        reset: function () {
            dataTransfer = new DataTransfer();
            fileInput.files = dataTransfer.files;
            chipList.innerHTML = '';
        }
    };
};

// ── 글자수 카운터 ──
App.initCharCounter = function (inputEl, countEl, maxLen, threshold) {
    if (!inputEl || !countEl) { return; }
    const limit = threshold ?? Math.floor(maxLen * 0.9);
    countEl.textContent = inputEl.value.length + '/' + maxLen;
    inputEl.addEventListener('input', function () {
        const len = this.value.length;
        countEl.textContent = len + '/' + maxLen;
        countEl.classList.toggle('near-limit', len >= limit);
    });
};

// ── 비밀번호 검증 ──
App.initPasswordValidation = function (passwordInputId, confirmInputId, errorClass, errorMessage) {
    const passwordInput = document.getElementById(passwordInputId);
    const confirmInput  = document.getElementById(confirmInputId);
    if (!passwordInput || !confirmInput) { return; }

    const msgClass = errorClass ?? 'input-error-msg';
    const msgText  = errorMessage ?? '비밀번호가 일치하지 않습니다.';

    function getOrCreateMsg() {
        let msg = confirmInput.parentElement.querySelector('.' + msgClass);
        if (!msg) {
            msg = document.createElement('span');
            msg.className   = msgClass;
            msg.textContent = msgText;
            confirmInput.parentElement.appendChild(msg);
        }
        return msg;
    }

    function validate() {
        const mismatch = confirmInput.value.length > 0 && passwordInput.value !== confirmInput.value;
        getOrCreateMsg().classList.toggle('visible', mismatch);
        confirmInput.classList.toggle('input-error', mismatch);
    }

    passwordInput.addEventListener('input', validate);
    confirmInput.addEventListener('input', validate);

    const form = confirmInput.closest('form');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (passwordInput.value !== confirmInput.value) {
                e.preventDefault();
                getOrCreateMsg().classList.add('visible');
                confirmInput.classList.add('input-error');
                confirmInput.focus();
            }
        });
    }
};
