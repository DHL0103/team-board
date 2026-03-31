class CharCounter {

    #inputEl;
    #countEl;
    #maxLen;
    #limit;

    constructor(inputEl, countEl, maxLen, threshold) {
        if (!inputEl || !countEl) { return; }
        this.#inputEl = inputEl;
        this.#countEl = countEl;
        this.#maxLen  = maxLen;
        this.#limit   = threshold ?? Math.floor(maxLen * 0.9);

        this.#countEl.textContent = this.#inputEl.value.length + '/' + this.#maxLen;
        this.#inputEl.addEventListener('input', () => this.#update());
    }

    #update() {
        const len = this.#inputEl.value.length;
        this.#countEl.textContent = len + '/' + this.#maxLen;
        this.#countEl.classList.toggle('near-limit', len >= this.#limit);
    }
}
