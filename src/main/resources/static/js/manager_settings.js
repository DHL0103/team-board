// ── 에러 토스트 ──
window.addEventListener('load', () => {
    const toast = document.getElementById('errorToast');
    if (toast && toast.textContent.trim()) {
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }
});

// ── 글자수 카운터 ──
const settingsNameInput = document.getElementById('settingsName');
const settingsNameCount = document.getElementById('settingsNameCount');
if (settingsNameInput && settingsNameCount) {
    settingsNameCount.textContent = settingsNameInput.value.length + '/50';
    settingsNameInput.addEventListener('input', () => {
        const len = settingsNameInput.value.length;
        settingsNameCount.textContent = len + '/50';
        settingsNameCount.classList.toggle('near-limit', len >= 45);
    });
}

const settingsDescInput = document.getElementById('settingsDescription');
const settingsDescCount = document.getElementById('settingsDescCount');
if (settingsDescInput && settingsDescCount) {
    settingsDescCount.textContent = settingsDescInput.value.length + '/500';
    settingsDescInput.addEventListener('input', () => {
        const len = settingsDescInput.value.length;
        settingsDescCount.textContent = len + '/500';
        settingsDescCount.classList.toggle('near-limit', len >= 450);
    });
}
