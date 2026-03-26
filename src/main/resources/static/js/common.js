// ── 햄버거 메뉴 ──
(function () {
    const hamburger = document.getElementById('hamburger');
    const navMenu   = document.getElementById('nav-menu');
    if (!hamburger || !navMenu) { return; }

    hamburger.addEventListener('click', function () {
        navMenu.classList.toggle('open');
    });

    document.addEventListener('click', function (e) {
        if (!hamburger.contains(e.target) && !navMenu.contains(e.target)) {
            navMenu.classList.remove('open');
        }
    });
})();

// ── 보드 설명 접기/펼치기 ──
(function () {
    var desc = document.getElementById("boardDescription");
    var btn  = document.getElementById("btnDescToggle");
    if (!desc || !btn) { return; }

    desc.classList.add("page-meta--clamp");

    if (desc.scrollHeight > desc.clientHeight) {
        btn.style.display = "";
    }

    btn.addEventListener("click", function () {
        var clamped = desc.classList.toggle("page-meta--clamp");
        btn.textContent = clamped ? "자세히 보기" : "접기";
    });
})();

// ── App 네임스페이스 ──
const App = (function () {

    // ── private ──
    const _memberCache = {};

    // ── 토스트 ──
    function showToast(message) {
        const toast = document.getElementById("errorToast");
        if (!toast) { return; }
        toast.textContent = message;
        toast.classList.add("show");
        setTimeout(() => toast.classList.remove("show"), 3000);
    }

    // ── 마감일 칩 ──
    function initDueChips() {
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
    }

    // ── 날짜 피커 ──
    // prefix 예시: 'create' → create-due-days, create-month-label, btn-create-prev-month, ...
    function initDatePicker(prefix, { initialDate = null, formatDisplay = null } = {}) {
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
    }

    // ── 모달 ──
    function openModal(id) {
        document.getElementById(id).classList.add("open");
        document.body.style.overflow = "hidden";
    }

    function closeModal(id) {
        document.getElementById(id).classList.remove("open");
        document.body.style.overflow = "";
    }

    function initModals() {
        document.querySelectorAll('.modal-overlay').forEach(overlay => {
            overlay.addEventListener('click', e => {
                if (e.target === overlay) { closeModal(overlay.id); }
            });
        });
        document.querySelectorAll('.modal-close').forEach(btn => {
            btn.addEventListener('click', () => {
                const overlay = btn.closest('.modal-overlay');
                if (overlay) { closeModal(overlay.id); }
            });
        });
        document.addEventListener('keydown', e => {
            if (e.key === 'Escape') {
                document.querySelectorAll('.modal-overlay.open').forEach(m => closeModal(m.id));
            }
        });
    }

    // ── 글자수 카운터 ──
    function initCharCounter(inputEl, countEl, maxLen, threshold) {
        if (!inputEl || !countEl) { return; }
        const limit = threshold ?? Math.floor(maxLen * 0.9);
        countEl.textContent = inputEl.value.length + '/' + maxLen;
        inputEl.addEventListener('input', function () {
            const len = this.value.length;
            countEl.textContent = len + '/' + maxLen;
            countEl.classList.toggle('near-limit', len >= limit);
        });
    }

    // ── 페이지네이션 ──
    function renderPagination(paginationEl, totalPages, currentPage, onPageChange) {
        paginationEl.innerHTML = '';
        if (totalPages <= 1) { return; }

        const prev = document.createElement('button');
        prev.className = 'page-btn';
        prev.textContent = '이전';
        prev.disabled = currentPage === 1;
        prev.addEventListener('click', function () { onPageChange(currentPage - 1); });
        paginationEl.appendChild(prev);

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement('button');
            btn.className = 'page-btn' + (i === currentPage ? ' active' : '');
            btn.textContent = i;
            btn.addEventListener('click', (function (page) {
                return function () { onPageChange(page); };
            })(i));
            paginationEl.appendChild(btn);
        }

        const next = document.createElement('button');
        next.className = 'page-btn';
        next.textContent = '다음';
        next.disabled = currentPage === totalPages;
        next.addEventListener('click', function () { onPageChange(currentPage + 1); });
        paginationEl.appendChild(next);
    }

    function makePaginator(items, paginationEl, pageSize) {
        let currentPage = 1;

        function render() {
            const start = (currentPage - 1) * pageSize;
            const end   = start + pageSize;
            items.forEach(function (item, i) {
                item.style.display = (i >= start && i < end) ? '' : 'none';
            });
            const totalPages = Math.max(1, Math.ceil(items.length / pageSize));
            renderPagination(paginationEl, totalPages, currentPage, function (page) {
                currentPage = page;
                render();
            });
        }

        render();
    }

    // ── 파일 첨부 ──
    function initFileAttachment(fileInputId, attachBtnId, chipListId, maxSizeMB) {
        const fileInput = document.getElementById(fileInputId);
        const chipList  = document.getElementById(chipListId);
        const attachBtn = document.getElementById(attachBtnId);
        if (!fileInput || !chipList) { return; }

        const maxSize = (maxSizeMB ?? 10) * 1024 * 1024;
        let dataTransfer = new DataTransfer();

        if (attachBtn) {
            attachBtn.addEventListener('click', function () { fileInput.click(); });
        }

        fileInput.addEventListener('change', function () {
            for (const file of fileInput.files) {
                if (file.size > maxSize) {
                    showToast(file.name + ': 파일 크기는 ' + (maxSizeMB ?? 10) + 'MB를 초과할 수 없습니다.');
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
    }

    // ── 비밀번호 검증 ──
    function initPasswordValidation(passwordInputId, confirmInputId, errorClass, errorMessage) {
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
    }

    // ── 담당자 피커 ──
    function initAssigneePicker({ addBtnId, dropdownId, searchId, optionListId, selectedListId, pickerId, formId, inputName = 'assigneeIds' }) {
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
    }

    // ── 회원 이름 조회 ──
    async function fetchUsername(memberId) {
        if (!memberId) { return "알 수 없음"; }
        if (_memberCache[memberId]) { return _memberCache[memberId]; }

        try {
            const res = await fetch(`/member/${memberId}`);
            if (!res.ok) { throw new Error("Network response was not ok"); }
            const member = await res.json();
            _memberCache[memberId] = member.username;
            return member.username;
        } catch {
            return "알 수 없음";
        }
    }

    return {
        showToast,
        initDueChips,
        initDatePicker,
        openModal,
        closeModal,
        initModals,
        initCharCounter,
        renderPagination,
        makePaginator,
        initFileAttachment,
        initPasswordValidation,
        initAssigneePicker,
        fetchUsername,
    };
})();

// ── 자동 실행 ──
App.initDueChips();
App.initModals();

// ── 에러 토스트 자동 표시 ──
window.addEventListener('load', function () {
    const toast = document.getElementById('errorToast');
    if (toast && toast.textContent.trim()) {
        toast.classList.add('show');
        setTimeout(function () { toast.classList.remove('show'); }, 3000);
    }
});