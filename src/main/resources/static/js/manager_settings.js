// ── 에러 토스트 ──
window.addEventListener('load', () => {
    const toast = document.getElementById('errorToast');
    if (toast && toast.textContent.trim()) {
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }
});
