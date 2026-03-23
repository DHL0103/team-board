/**
 * 공통 페이지네이션 유틸
 * @param {HTMLElement} container  - 페이지네이션을 렌더링할 요소
 * @param {number}      currentPage - 현재 페이지 (1-based)
 * @param {number}      totalPages  - 전체 페이지 수
 * @param {Function}    onPageChange - 페이지 변경 콜백 (newPage: number) => void
 */
function createPagination(container, currentPage, totalPages, onPageChange) {
    container.innerHTML = '';
    if (totalPages <= 1) { return; }

    var WINDOW = 2; // 현재 페이지 기준 ±2 = 최대 5개 번호 노출

    // 이전 버튼
    var prev = document.createElement('button');
    prev.className = 'page-btn';
    prev.textContent = '이전';
    prev.disabled = currentPage === 1;
    prev.addEventListener('click', function () { onPageChange(currentPage - 1); });
    container.appendChild(prev);

    // 페이지 번호 + ··· 생략 부호
    var lastRendered = 0;
    for (var i = 1; i <= totalPages; i++) {
        var inWindow = i >= currentPage - WINDOW && i <= currentPage + WINDOW;
        if (i === 1 || i === totalPages || inWindow) {
            if (lastRendered && i - lastRendered > 1) {
                var dots = document.createElement('span');
                dots.className = 'page-dots';
                dots.textContent = '···';
                container.appendChild(dots);
            }
            (function (page) {
                var btn = document.createElement('button');
                btn.className = 'page-btn' + (page === currentPage ? ' active' : '');
                btn.textContent = page;
                btn.addEventListener('click', function () { onPageChange(page); });
                container.appendChild(btn);
            })(i);
            lastRendered = i;
        }
    }

    // 다음 버튼
    var next = document.createElement('button');
    next.className = 'page-btn';
    next.textContent = '다음';
    next.disabled = currentPage === totalPages;
    next.addEventListener('click', function () { onPageChange(currentPage + 1); });
    container.appendChild(next);
}
