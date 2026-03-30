
class DatePicker {

    #daysEl;
    #monthLabel;
    #dueValueEl;
    #displayEl;
    #fmt;
    #today;
    #selectedDate = null;
    #currentDate;

    constructor(prefix, { initialDate = null, formatDisplay = null } = {}) {
        this.#daysEl     = document.getElementById(`${prefix}-due-days`);
        this.#monthLabel = document.getElementById(`${prefix}-month-label`);
        this.#dueValueEl = document.getElementById(`${prefix}-due-value`);
        this.#displayEl  = document.getElementById(`${prefix}-selected-display`);
        const prevBtn    = document.getElementById(`btn-${prefix}-prev-month`);
        const nextBtn    = document.getElementById(`btn-${prefix}-next-month`);
        const clearBtn   = document.getElementById(`btn-${prefix}-clear-due`);
        if (!this.#daysEl || !this.#monthLabel) { return; }

        this.#fmt = formatDisplay || ((m, d) => `${m}.${d} 마감`);
        this.#today = new Date();
        this.#today.setHours(0, 0, 0, 0);
        this.#currentDate = new Date();

        if (initialDate) {
            this.#selectedDate = new Date(initialDate + 'T00:00:00');
            this.#currentDate  = new Date(this.#selectedDate);
            const m = String(this.#selectedDate.getMonth() + 1).padStart(2, '0');
            const d = String(this.#selectedDate.getDate()).padStart(2, '0');
            if (this.#displayEl) { this.#displayEl.textContent = this.#fmt(m, d); }
        }

        if (prevBtn)  { prevBtn.addEventListener('click', () => { this.#currentDate.setMonth(this.#currentDate.getMonth() - 1); this.#render(); }); }
        if (nextBtn)  { nextBtn.addEventListener('click', () => { this.#currentDate.setMonth(this.#currentDate.getMonth() + 1); this.#render(); }); }
        if (clearBtn) { clearBtn.addEventListener('click', () => this.#clear()); }

        this.#render();
    }

    #render() {
        const year  = this.#currentDate.getFullYear();
        const month = this.#currentDate.getMonth();
        this.#monthLabel.textContent = `${year}년 ${month + 1}월`;
        this.#daysEl.innerHTML = '';

        const firstDay = new Date(year, month, 1).getDay();
        const lastDate = new Date(year, month + 1, 0).getDate();

        for (let i = 0; i < firstDay; i++) {
            const el = document.createElement('div');
            el.className = 'due-day empty';
            this.#daysEl.appendChild(el);
        }

        for (let d = 1; d <= lastDate; d++) {
            const el = document.createElement('div');
            el.className = 'due-day';
            el.textContent = d;

            const thisDate = new Date(year, month, d);
            const isPast   = thisDate < this.#today;
            const isToday  = thisDate.getTime() === this.#today.getTime();

            if (isPast)  { el.classList.add('past'); }
            if (isToday) { el.classList.add('today'); }
            if (this.#selectedDate && thisDate.getTime() === this.#selectedDate.getTime()) { el.classList.add('selected'); }

            if (!isPast) {
                el.addEventListener('click', () => {
                    this.#selectedDate = thisDate;
                    const y  = String(year);
                    const m  = String(month + 1).padStart(2, '0');
                    const dd = String(d).padStart(2, '0');
                    if (this.#dueValueEl) { this.#dueValueEl.value = `${y}-${m}-${dd}T00:00:00`; }
                    if (this.#displayEl)  { this.#displayEl.textContent = this.#fmt(m, dd); }
                    this.#render();
                });
            }
            this.#daysEl.appendChild(el);
        }
    }

    #clear() {
        this.#selectedDate = null;
        if (this.#dueValueEl) { this.#dueValueEl.value = ''; }
        if (this.#displayEl)  { this.#displayEl.textContent = '선택 안 함'; }
        this.#render();
    }
}
