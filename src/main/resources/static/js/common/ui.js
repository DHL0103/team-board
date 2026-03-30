// ── 토스트 ──
App.showToast = function (message) {
    const toast = document.getElementById("errorToast");
    if (!toast) { return; }
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 3000);
};

// ── 모달 ──
App.openModal = function (id) {
    document.getElementById(id).classList.add("open");
    document.body.style.overflow = "hidden";
};

App.closeModal = function (id) {
    document.getElementById(id).classList.remove("open");
    document.body.style.overflow = "";
};

App.initModals = function () {
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', e => {
            if (e.target === overlay) { App.closeModal(overlay.id); }
        });
    });
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const overlay = btn.closest('.modal-overlay');
            if (overlay) { App.closeModal(overlay.id); }
        });
    });
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-overlay.open').forEach(m => App.closeModal(m.id));
        }
    });
};

// ── 마감일 칩 ──
App.initDueChips = function () {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    document.querySelectorAll(".due-chip").forEach(chip => {
        const due = chip.dataset.due;
        if (!due) { chip.classList.add("none"); return; }
        const dueDate = new Date(due);
        const diffDays = Math.floor((dueDate - today) / (1000 * 60 * 60 * 24));
        const label = chip.querySelector(".due-label");
        if (diffDays < 0)        { chip.classList.add("over"); label.textContent += " 초과"; }
        else if (diffDays === 0) { chip.classList.add("warn"); label.textContent = "오늘 마감"; }
        else if (diffDays === 1) { chip.classList.add("warn"); label.textContent = "내일 마감"; }
        else if (diffDays <= 3)  { chip.classList.add("warn"); label.textContent += " 마감"; }
        else                     { chip.classList.add("safe"); label.textContent += " 마감"; }
    });
};

// ── 날짜 피커 ──
App.initDatePicker = function (prefix, { initialDate = null, formatDisplay = null } = {}) {
    const daysEl     = document.getElementById(`${prefix}-due-days`);
    const monthLabel = document.getElementById(`${prefix}-month-label`);
    const dueValueEl = document.getElementById(`${prefix}-due-value`);
    const displayEl  = document.getElementById(`${prefix}-selected-display`);
    const prevBtn    = document.getElementById(`btn-${prefix}-prev-month`);
    const nextBtn    = document.getElementById(`btn-${prefix}-next-month`);
    const clearBtn   = document.getElementById(`btn-${prefix}-clear-due`);
    if (!daysEl || !monthLabel) { return; }

    const fmt = formatDisplay || ((m, d) => `${m}.${d} 마감`);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let selectedDate = null;
    let currentDate  = new Date();

    if (initialDate) {
        selectedDate = new Date(initialDate + 'T00:00:00');
        currentDate  = new Date(selectedDate);
        const m = String(selectedDate.getMonth() + 1).padStart(2, '0');
        const d = String(selectedDate.getDate()).padStart(2, '0');
        if (displayEl) { displayEl.textContent = fmt(m, d); }
    }

    function render() {
        const year  = currentDate.getFullYear();
        const month = currentDate.getMonth();
        monthLabel.textContent = `${year}년 ${month + 1}월`;
        daysEl.innerHTML = '';

        const firstDay = new Date(year, month, 1).getDay();
        const lastDate = new Date(year, month + 1, 0).getDate();

        for (let i = 0; i < firstDay; i++) {
            const el = document.createElement('div');
            el.className = 'due-day empty';
            daysEl.appendChild(el);
        }

        for (let d = 1; d <= lastDate; d++) {
            const el = document.createElement('div');
            el.className = 'due-day';
            el.textContent = d;

            const thisDate = new Date(year, month, d);
            const isPast   = thisDate < today;
            const isToday  = thisDate.getTime() === today.getTime();

            if (isPast)  { el.classList.add('past'); }
            if (isToday) { el.classList.add('today'); }
            if (selectedDate && thisDate.getTime() === selectedDate.getTime()) { el.classList.add('selected'); }

            if (!isPast) {
                el.addEventListener('click', () => {
                    selectedDate = thisDate;
                    const y  = String(year);
                    const m  = String(month + 1).padStart(2, '0');
                    const dd = String(d).padStart(2, '0');
                    if (dueValueEl) { dueValueEl.value = `${y}-${m}-${dd}T00:00:00`; }
                    if (displayEl)  { displayEl.textContent = fmt(m, dd); }
                    render();
                });
            }
            daysEl.appendChild(el);
        }
    }

    function clear() {
        selectedDate = null;
        if (dueValueEl) { dueValueEl.value = ''; }
        if (displayEl)  { displayEl.textContent = '선택 안 함'; }
        render();
    }

    if (prevBtn)  { prevBtn.addEventListener('click', () => { currentDate.setMonth(currentDate.getMonth() - 1); render(); }); }
    if (nextBtn)  { nextBtn.addEventListener('click', () => { currentDate.setMonth(currentDate.getMonth() + 1); render(); }); }
    if (clearBtn) { clearBtn.addEventListener('click', clear); }

    render();
};

// ── 담당자 피커 ──
App.initAssigneePicker = function ({ addBtnId, dropdownId, searchId, optionListId, selectedListId, pickerId, formId, inputName = 'assigneeIds' }) {
    const addBtn       = document.getElementById(addBtnId);
    const dropdown     = document.getElementById(dropdownId);
    const search       = searchId ? document.getElementById(searchId) : null;
    const optionList   = document.getElementById(optionListId);
    const selectedList = document.getElementById(selectedListId);
    if (!addBtn || !dropdown || !optionList || !selectedList) { return null; }

    const selected = new Map();

    selectedList.querySelectorAll('.selected-assignee-chip').forEach(chip => {
        selected.set(chip.dataset.memberId, chip.dataset.username);
        const opt = optionList.querySelector(`[data-member-id="${chip.dataset.memberId}"]`);
        if (opt) { opt.style.display = 'none'; }
    });

    const original = new Map(selected);

    function renderChips() {
        selectedList.innerHTML = '';
        selected.forEach((username, memberId) => {
            const chip = document.createElement('span');
            chip.className = 'selected-assignee-chip';
            chip.dataset.memberId = memberId;
            chip.dataset.username = username;
            chip.innerHTML = `<span class="assignee-chip-name">${username}</span>`
                + `<button type="button" class="selected-assignee-remove" data-member-id="${memberId}">×</button>`;
            selectedList.appendChild(chip);
        });
    }

    addBtn.addEventListener('click', e => {
        e.stopPropagation();
        dropdown.classList.toggle('open');
        if (dropdown.classList.contains('open') && search) { search.focus(); }
    });

    if (pickerId) {
        document.addEventListener('click', e => {
            if (!e.target.closest('#' + pickerId)) { dropdown.classList.remove('open'); }
        });
    }

    if (search) {
        search.addEventListener('input', () => {
            const query = search.value.trim().toLowerCase();
            optionList.querySelectorAll('.assignee-option').forEach(opt => {
                if (selected.has(opt.dataset.memberId)) { opt.style.display = 'none'; return; }
                opt.style.display = opt.dataset.username.toLowerCase().includes(query) ? '' : 'none';
            });
        });
    }

    optionList.addEventListener('click', e => {
        const opt = e.target.closest('.assignee-option');
        if (!opt) { return; }
        selected.set(opt.dataset.memberId, opt.dataset.username);
        opt.style.display = 'none';
        renderChips();
        if (search) { search.value = ''; }
        dropdown.classList.remove('open');
    });

    selectedList.addEventListener('click', e => {
        const removeBtn = e.target.closest('.selected-assignee-remove');
        if (!removeBtn) { return; }
        const memberId = removeBtn.dataset.memberId;
        selected.delete(memberId);
        const opt = optionList.querySelector(`[data-member-id="${memberId}"]`);
        if (opt) { opt.style.display = ''; }
        renderChips();
    });

    if (formId) {
        const form = document.getElementById(formId);
        if (form) {
            form.addEventListener('submit', () => {
                form.querySelectorAll(`input[name="${inputName}"]`).forEach(el => el.remove());
                selected.forEach((username, memberId) => {
                    const input = document.createElement('input');
                    input.type  = 'hidden';
                    input.name  = inputName;
                    input.value = memberId;
                    form.appendChild(input);
                });
            });
        }
    }

    return {
        reset() {
            selected.clear();
            original.forEach((username, memberId) => selected.set(memberId, username));
            renderChips();
            optionList.querySelectorAll('.assignee-option').forEach(opt => {
                opt.style.display = selected.has(opt.dataset.memberId) ? 'none' : '';
            });
            if (search) { search.value = ''; }
            dropdown.classList.remove('open');
        }
    };
};

// ── 포스트 카드 공통 ──
const _STATUS_META = {
    PROGRESS:  { label: '진행 중',   cls: 'chip-progress'  },
    REJECTED:  { label: '반려',      cls: 'chip-rejected'  },
    REQUESTED: { label: '승인 요청', cls: 'chip-requested' },
    APPROVED:  { label: '완료',      cls: 'chip-completed' },
};

function _applyDueChip(chipEl, labelEl, dueDate) {
    if (!dueDate) { chipEl.classList.add('none'); return; }
    const today = new Date(); today.setHours(0, 0, 0, 0);
    const due   = new Date(dueDate + 'T00:00:00');
    const diffDays = Math.floor((due - today) / 86400000);
    if (diffDays < 0)        { chipEl.classList.add('over'); labelEl.textContent += ' 초과'; }
    else if (diffDays === 0) { chipEl.classList.add('warn'); labelEl.textContent = '오늘 마감'; }
    else if (diffDays === 1) { chipEl.classList.add('warn'); labelEl.textContent = '내일 마감'; }
    else if (diffDays <= 3)  { chipEl.classList.add('warn'); labelEl.textContent += ' 마감'; }
    else                     { chipEl.classList.add('safe'); labelEl.textContent += ' 마감'; }
}

App.createPostCard = function (post, { boardId, myPostIds, asLink = false } = {}) {
    const card = document.createElement(asLink ? 'a' : 'div');
    card.className = asLink ? 'post-list-card' : 'board-card';
    if (asLink) {
        card.href = `/board/${boardId}/post/${post.id}`;
    } else {
        card.dataset.href = `/board/${boardId}/post/${post.id}`;
    }

    const top = document.createElement('div');
    top.className = 'card-top';

    const titleEl = document.createElement('div');
    titleEl.className = 'card-title';
    titleEl.textContent = post.title;

    const meta = _STATUS_META[post.status] || { label: post.status, cls: '' };
    const chipEl = document.createElement('span');
    chipEl.className = `status-chip ${meta.cls}`;
    chipEl.textContent = meta.label;

    top.appendChild(titleEl);
    top.appendChild(chipEl);

    const contentEl = document.createElement('div');
    contentEl.className = 'card-content';
    contentEl.textContent = post.plainContent || '';

    const footer = document.createElement('div');
    footer.className = 'card-footer';

    const cardLeft = document.createElement('div');
    cardLeft.className = 'card-left';

    const dueDate = post.dueDate ? post.dueDate.substring(0, 10) : null;
    const dueChip = document.createElement('span');
    dueChip.className = 'due-chip';
    dueChip.dataset.due = dueDate || '';
    dueChip.innerHTML = '<svg width="10" height="10"><use href="/img/icons.svg#icon-calendar"></use></svg>';

    const dueLabel = document.createElement('span');
    dueLabel.className = 'due-label';
    dueLabel.textContent = dueDate ? dueDate.substring(5).replace('-', '.') : '기한 없음';
    _applyDueChip(dueChip, dueLabel, dueDate);
    dueChip.appendChild(dueLabel);
    cardLeft.appendChild(dueChip);

    if (post.hasFiles) {
        cardLeft.insertAdjacentHTML('beforeend',
            '<svg class="card-indicator-icon" width="11" height="11" title="첨부파일 있음"><use href="/img/icons.svg#icon-file"></use></svg>');
    }
    if (post.content && post.content.includes('<img')) {
        cardLeft.insertAdjacentHTML('beforeend',
            '<svg class="card-indicator-icon" width="11" height="11" title="인라인 이미지 있음"><use href="/img/icons.svg#icon-image"></use></svg>');
    }

    const cardRight = document.createElement('div');
    cardRight.className = 'card-right';

    if (myPostIds && myPostIds.has(post.id)) {
        cardRight.insertAdjacentHTML('beforeend',
            '<svg class="card-mine-icon" width="12" height="12"><use href="/img/icons.svg#icon-user"></use></svg>');
    }

    const dateEl = document.createElement('span');
    dateEl.className = 'card-date';
    dateEl.textContent = post.createdAt ? post.createdAt.substring(5, 10).replace('-', '.') : '';
    cardRight.appendChild(dateEl);

    footer.appendChild(cardLeft);
    footer.appendChild(cardRight);
    card.appendChild(top);
    card.appendChild(contentEl);
    card.appendChild(footer);

    if (!asLink) {
        card.addEventListener('click', () => { window.location.href = card.dataset.href; });
    }

    return card;
};

// ── 자동 실행 ──
App.initDueChips();
App.initModals();
