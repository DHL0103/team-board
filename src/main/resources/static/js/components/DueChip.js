class DueChip {

    static #initialized = false;

    static init() {
        if (DueChip.#initialized) { return; }
        DueChip.#initialized = true;

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        document.querySelectorAll('.due-chip').forEach(chip => {
            const due = chip.dataset.due;
            if (!due) { chip.classList.add('none'); return; }
            const dueDate = new Date(due);
            const diffDays = Math.floor((dueDate - today) / (1000 * 60 * 60 * 24));
            const label = chip.querySelector('.due-label');
            if (diffDays < 0)        { chip.classList.add('over'); label.textContent += ' 초과'; }
            else if (diffDays === 0) { chip.classList.add('warn'); label.textContent = '오늘 마감'; }
            else if (diffDays === 1) { chip.classList.add('warn'); label.textContent = '내일 마감'; }
            else if (diffDays <= 3)  { chip.classList.add('warn'); label.textContent += ' 마감'; }
            else                     { chip.classList.add('safe'); label.textContent += ' 마감'; }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => DueChip.init());
