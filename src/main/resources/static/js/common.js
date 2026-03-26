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

// ── 토스트 유틸 ──
function showToast(message) {
    const toast = document.getElementById("errorToast");
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 3000);
}

// ── 마감일 칩 유틸 ──
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

initDueChips();

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

// ── 모달 유틸 ──
function openModal(id) {
    document.getElementById(id).classList.add("open");
    document.body.style.overflow = "hidden";
}

function closeModal(id) {
    document.getElementById(id).classList.remove("open");
    document.body.style.overflow = "";
}

// ── 글자수 카운터 유틸 ──
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

// ── 페이지네이션 버튼 렌더 유틸 ──
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

// ── 아이템 배열 페이지네이터 (show/hide 포함) ──
function makePaginator(items, paginationEl, pageSize) {
    let currentPage = 1;

    function render() {
        const start = (currentPage - 1) * pageSize;
        const end = start + pageSize;
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

// ── 파일 첨부 유틸 ──
function initFileAttachment(fileInputId, attachBtnId, chipListId, maxSizeMB) {
    const fileInput = document.getElementById(fileInputId);
    const chipList = document.getElementById(chipListId);
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
        const idx = parseInt(e.target.dataset.index);
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

// ── 비밀번호 검증 유틸 ──
function initPasswordValidation(passwordInputId, confirmInputId, errorClass, errorMessage) {
    const passwordInput = document.getElementById(passwordInputId);
    const confirmInput = document.getElementById(confirmInputId);
    if (!passwordInput || !confirmInput) { return; }

    const msgClass = errorClass ?? 'input-error-msg';
    const msgText = errorMessage ?? '비밀번호가 일치하지 않습니다.';

    function getOrCreateMsg() {
        let msg = confirmInput.parentElement.querySelector('.' + msgClass);
        if (!msg) {
            msg = document.createElement('span');
            msg.className = msgClass;
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

// ── 에러 토스트 자동 표시 ──
window.addEventListener('load', function () {
    const toast = document.getElementById('errorToast');
    if (toast && toast.textContent.trim()) {
        toast.classList.add('show');
        setTimeout(function () { toast.classList.remove('show'); }, 3000);
    }
});

// ── 회원 이름 캐싱 및 조회 유틸 ──
const memberCache = {};

async function fetchUsername(memberId) {
    if (!memberId) return "알 수 없음";
    if (memberCache[memberId]) return memberCache[memberId];

    try {
        const res = await fetch(`/member/${memberId}`);
        if (!res.ok) throw new Error("Network response was not ok");
        const member = await res.json();
        memberCache[memberId] = member.username;
        return member.username;
    } catch {
        return "알 수 없음";
    }
}
