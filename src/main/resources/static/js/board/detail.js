// 카드 클릭 → 상세 페이지 이동
document.querySelectorAll('.board-card[data-href]').forEach(card => {
    card.addEventListener('click', () => {
        window.location.href = card.dataset.href;
    });
});

// 작업 추가 모달
const btnCreatePost = document.getElementById('btn-create-post');

btnCreatePost.addEventListener('click', () => {
    openModal('createModal');
});

function closeCreateModal() {
    closeModal('createModal');
    createFileManager.reset();
    if (createAssigneePicker) { createAssigneePicker.reset(); }
}

const btnCreateClose = document.getElementById('btn-create-close');
btnCreateClose.addEventListener('click', closeCreateModal);

// 마감일 피커 (생성 모달)
initDatePicker('create', { formatDisplay: (m, d) => `${parseInt(m)}/${parseInt(d)}` });

// 파일 첨부
const createFileManager = initFileAttachment('create-file-input', 'btn-create-file-attach', 'create-file-chip-list');

// 담당자 선택
const createAssigneePicker = initAssigneePicker({
    addBtnId:      'btn-add-assignee',
    dropdownId:    'assignee-dropdown',
    searchId:      'create-assignee-search',
    optionListId:  'assignee-option-list',
    selectedListId:'selected-assignee-list',
    pickerId:      'assignee-picker',
    formId:        'create-post-form',
});

// ── 제목 글자수 카운터 ──
initCharCounter(
    document.getElementById('create-title'),
    document.getElementById('createTitleCount'),
    100, 90
);
