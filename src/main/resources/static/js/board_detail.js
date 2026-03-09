initDueChips();

// 카드 클릭 → 상세 페이지 이동
document.querySelectorAll('.board-card[data-href]').forEach(card => {
    card.addEventListener('click', () => {
        window.location.href = card.dataset.href;
    });
});

// 작업 추가 모달
const createModal = document.getElementById('createModal');
const btnCreatePost = document.getElementById('btn-create-post');
const btnCreateClose = document.getElementById('btn-create-close');

btnCreatePost.addEventListener('click', () => {
    createModal.classList.add('open');
});

btnCreateClose.addEventListener('click', () => {
    createModal.classList.remove('open');
});

createModal.addEventListener('click', (e) => {
    if (e.target === createModal) {
        createModal.classList.remove('open');
    }
});

// 마감일 피커 (생성 모달)
const createDueValue = document.getElementById('create-due-value');
const createDueDays = document.getElementById('create-due-days');
const createMonthLabel = document.getElementById('create-month-label');
const createSelectedDisplay = document.getElementById('create-selected-display');
const btnCreatePrev = document.getElementById('btn-create-prev-month');
const btnCreateNext = document.getElementById('btn-create-next-month');
const btnCreateClear = document.getElementById('btn-create-clear-due');

let createCurrentDate = new Date();
let createSelectedDate = null;

function renderCreateCalendar() {
    const year = createCurrentDate.getFullYear();
    const month = createCurrentDate.getMonth();
    createMonthLabel.textContent = `${year}년 ${month + 1}월`;

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    createDueDays.innerHTML = '';

    for (let i = 0; i < firstDay; i++) {
        const empty = document.createElement('div');
        empty.classList.add('due-day', 'empty');
        createDueDays.appendChild(empty);
    }

    for (let d = 1; d <= daysInMonth; d++) {
        const day = document.createElement('div');
        day.classList.add('due-day');
        day.textContent = d;

        const date = new Date(year, month, d);
        date.setHours(0, 0, 0, 0);

        if (date < today) {
            day.classList.add('past');
        } else if (date.getTime() === today.getTime()) {
            day.classList.add('today');
        }

        if (createSelectedDate && date.getTime() === createSelectedDate.getTime()) {
            day.classList.add('selected');
        }

        day.addEventListener('click', () => {
            if (day.classList.contains('past')) {
                return;
            }
            createSelectedDate = date;
            const formatted = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
            createDueValue.value = formatted + 'T00:00:00';
            createSelectedDisplay.textContent = `${month + 1}/${d}`;
            renderCreateCalendar();
        });

        createDueDays.appendChild(day);
    }
}

btnCreatePrev.addEventListener('click', () => {
    createCurrentDate.setMonth(createCurrentDate.getMonth() - 1);
    renderCreateCalendar();
});

btnCreateNext.addEventListener('click', () => {
    createCurrentDate.setMonth(createCurrentDate.getMonth() + 1);
    renderCreateCalendar();
});

btnCreateClear.addEventListener('click', () => {
    createSelectedDate = null;
    createDueValue.value = '';
    createSelectedDisplay.textContent = '선택 안 함';
    renderCreateCalendar();
});

// 파일 첨부
const createFileInput = document.getElementById('create-file-input');
const btnCreateFileAttach = document.getElementById('btn-create-file-attach');
const createFileChipList = document.getElementById('create-file-chip-list');

btnCreateFileAttach.addEventListener('click', () => {
    createFileInput.click();
});

createFileInput.addEventListener('change', () => {
    createFileChipList.innerHTML = '';
    Array.from(createFileInput.files).forEach(file => {
        const chip = document.createElement('span');
        chip.classList.add('file-chip');
        chip.textContent = file.name;
        createFileChipList.appendChild(chip);
    });
});

renderCreateCalendar();
