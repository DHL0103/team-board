class BoardDetailPage {

    static #initialized = false;
    static #createFileAttachment;
    static #createAssigneePicker;

    static #boardId;
    static #myPostIds;
    static #PAGE_SIZE = 8;

    static #STATUS_META = {
        PROGRESS:  { label: '진행 중',   cls: 'chip-progress'  },
        REJECTED:  { label: '반려',      cls: 'chip-rejected'  },
        REQUESTED: { label: '승인 요청', cls: 'chip-requested' },
        APPROVED:  { label: '완료',      cls: 'chip-completed' },
    };

    static #COLUMNS = [
        { status: 'PROGRESS',  bodyId: 'col-body-progress',  countId: 'col-count-progress',  sentinelId: 'col-sentinel-progress'  },
        { status: 'REQUESTED', bodyId: 'col-body-requested', countId: 'col-count-requested', sentinelId: 'col-sentinel-requested' },
        { status: 'APPROVED',  bodyId: 'col-body-approved',  countId: 'col-count-approved',  sentinelId: 'col-sentinel-approved'  },
    ];

    static init() {
        if (BoardDetailPage.#initialized) { return; }
        BoardDetailPage.#initialized = true;

        BoardDetailPage.#boardId   = document.body.dataset.boardId;
        BoardDetailPage.#myPostIds = new Set(JSON.parse(document.body.dataset.myPostIds || '[]'));

        BoardDetailPage.#COLUMNS.forEach(col => BoardDetailPage.#initColumn(col));
        BoardDetailPage.#initCreateModal();
        BoardDetailPage.#initLeaveBoardModal();
    }

    static #applyDueChip(chipEl, labelEl, dueDate) {
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

    static #createPostCard(post) {
        const boardId = BoardDetailPage.#boardId;
        const card = document.createElement('div');
        card.className = 'board-card';
        card.dataset.href = `/board/${boardId}/post/${post.id}`;

        const top = document.createElement('div');
        top.className = 'card-top';

        const titleEl = document.createElement('div');
        titleEl.className = 'card-title';
        titleEl.textContent = post.title;

        const meta = BoardDetailPage.#STATUS_META[post.status] || { label: post.status, cls: '' };
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
        BoardDetailPage.#applyDueChip(dueChip, dueLabel, dueDate);
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

        if (BoardDetailPage.#myPostIds.has(post.id)) {
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

        card.addEventListener('click', () => { window.location.href = card.dataset.href; });

        return card;
    }

    static #initColumn(col) {
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
                const res  = await fetch(`/api/board/${BoardDetailPage.#boardId}/posts?status=${col.status}&page=${page}&size=${BoardDetailPage.#PAGE_SIZE}`);
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
                    data.posts.forEach(post => colBody.insertBefore(BoardDetailPage.#createPostCard(post), sentinel));
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

    static #initCreateModal() {
        const btnCreate = document.getElementById('btn-create-post');
        const btnClose  = document.getElementById('btn-create-close');

        if (btnCreate) { btnCreate.addEventListener('click', () => Modal.open('createModal')); }
        if (btnClose) { btnClose.addEventListener('click', () => BoardDetailPage.#closeCreateModal()); }

        new DatePicker('create', { formatDisplay: (m, d) => `${parseInt(m)}/${parseInt(d)}` });
        BoardDetailPage.#createFileAttachment = new FileAttachment('create-file-input', 'btn-create-file-attach', 'create-file-chip-list');
        BoardDetailPage.#createAssigneePicker = new AssigneePicker({
            addBtnId:      'btn-add-assignee',
            dropdownId:    'assignee-dropdown',
            searchId:      'create-assignee-search',
            optionListId:  'assignee-option-list',
            selectedListId:'selected-assignee-list',
            pickerId:      'assignee-picker',
            formId:        'create-post-form',
        });
        new CharCounter(document.getElementById('create-title'), document.getElementById('createTitleCount'), 100, 90);
    }

    static #closeCreateModal() {
        Modal.close('createModal');
        if (BoardDetailPage.#createFileAttachment) { BoardDetailPage.#createFileAttachment.reset(); }
        if (BoardDetailPage.#createAssigneePicker) { BoardDetailPage.#createAssigneePicker.reset(); }
    }

    static #initLeaveBoardModal() {
        const btnLeave = document.getElementById('btn-leave-board');
        if (!btnLeave) { return; }

        btnLeave.addEventListener('click', () => Modal.open('leaveBoardModal'));
        document.getElementById('leaveBoardCancel')?.addEventListener('click', () => Modal.close('leaveBoardModal'));
        document.getElementById('leaveBoardConfirm')?.addEventListener('click', () => {
            document.getElementById('form-leave-board')?.submit();
        });
    }
}

document.addEventListener('DOMContentLoaded', () => BoardDetailPage.init());
