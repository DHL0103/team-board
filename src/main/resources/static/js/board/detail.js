const boardId    = document.body.dataset.boardId;
const myPostIds  = new Set(JSON.parse(document.body.dataset.myPostIds || '[]'));

const PAGE_SIZE = 8;

const COLUMNS = [
    { status: 'PROGRESS',  bodyId: 'col-body-progress',  countId: 'col-count-progress',  sentinelId: 'col-sentinel-progress'  },
    { status: 'REQUESTED', bodyId: 'col-body-requested', countId: 'col-count-requested', sentinelId: 'col-sentinel-requested' },
    { status: 'APPROVED',  bodyId: 'col-body-approved',  countId: 'col-count-approved',  sentinelId: 'col-sentinel-approved'  },
];


function initColumn(col) {
    const colBody  = document.getElementById(col.bodyId);
    const sentinel = document.getElementById(col.sentinelId);
    const countEl  = document.getElementById(col.countId);

    let page    = 0;
    let loading = false;
    let done    = false;

    const obs = new IntersectionObserver(async (entries) => {
        if (!entries[0].isIntersecting || loading || done) { return; }
        loading = true;

        try {
            const res  = await fetch(`/api/board/${boardId}/posts?status=${col.status}&page=${page}&size=${PAGE_SIZE}`);
            const data = await res.json();

            if (page === 0 && countEl) {
                countEl.textContent = data.totalCount;
            }

            if (data.posts.length === 0 && page === 0) {
                colBody.insertBefore(
                    (() => {
                        const el = document.createElement('div');
                        el.className = 'empty-col';
                        el.innerHTML =
                            '<div class="empty-icon"><svg width="14" height="14"><use href="/img/icons.svg#icon-alert"></use></svg></div>' +
                            '<span>작업이 없습니다</span>';
                        return el;
                    })(),
                    sentinel
                );
            } else {
                data.posts.forEach(post => colBody.insertBefore(App.createPostCard(post, { boardId, myPostIds }), sentinel));
            }

            page++;

            if (!data.hasMore) {
                done = true;
                obs.disconnect();
            }
        } catch (e) {
            console.error('게시글 로드 실패', e);
        } finally {
            loading = false;
        }
    }, { root: colBody, rootMargin: '100px' });

    obs.observe(sentinel);
}

// ── 컬럼 초기화 ──
COLUMNS.forEach(initColumn);

// ── 작업 추가 모달 ──
document.getElementById('btn-create-post').addEventListener('click', () => App.openModal('createModal'));

function closeCreateModal() {
    App.closeModal('createModal');
    createFileManager.reset();
    if (createAssigneePicker) { createAssigneePicker.reset(); }
}

document.getElementById('btn-create-close').addEventListener('click', closeCreateModal);

App.initDatePicker('create', { formatDisplay: (m, d) => `${parseInt(m)}/${parseInt(d)}` });

const createFileManager = App.initFileAttachment('create-file-input', 'btn-create-file-attach', 'create-file-chip-list');

const createAssigneePicker = App.initAssigneePicker({
    addBtnId:      'btn-add-assignee',
    dropdownId:    'assignee-dropdown',
    searchId:      'create-assignee-search',
    optionListId:  'assignee-option-list',
    selectedListId:'selected-assignee-list',
    pickerId:      'assignee-picker',
    formId:        'create-post-form',
});

App.initCharCounter(
    document.getElementById('create-title'),
    document.getElementById('createTitleCount'),
    100, 90
);

// ── 보드 나가기 ──
const btnLeaveBoard    = document.getElementById('btn-leave-board');
const leaveBoardModal  = document.getElementById('leaveBoardModal');
const leaveBoardCancel = document.getElementById('leaveBoardCancel');
const leaveBoardConfirm = document.getElementById('leaveBoardConfirm');
const formLeaveBoard   = document.getElementById('form-leave-board');

if (btnLeaveBoard) {
    btnLeaveBoard.addEventListener('click', () => {
        App.openModal('leaveBoardModal');
    });
}

if (leaveBoardCancel) {
    leaveBoardCancel.addEventListener('click', () => {
        App.closeModal('leaveBoardModal');
    });
}

if (leaveBoardModal) {
    leaveBoardModal.addEventListener('click', e => {
        if (e.target === leaveBoardModal) { App.closeModal('leaveBoardModal'); }
    });
}

if (leaveBoardConfirm) {
    leaveBoardConfirm.addEventListener('click', () => {
        formLeaveBoard.submit();
    });
}
