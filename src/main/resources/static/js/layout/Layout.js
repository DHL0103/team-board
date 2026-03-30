class Layout {

    static init() {
        Layout.#bindHamburger();
        Layout.#bindBoardDescription();
        Layout.#showErrorToast();
    }

    static #bindHamburger() {
        const hamburger = document.getElementById('hamburger');
        const navMenu   = document.getElementById('nav-menu');
        if (!hamburger || !navMenu) { return; }

        hamburger.addEventListener('click', () => {
            navMenu.classList.toggle('open');
        });

        document.addEventListener('click', e => {
            if (!hamburger.contains(e.target) && !navMenu.contains(e.target)) {
                navMenu.classList.remove('open');
            }
        });
    }

    static #bindBoardDescription() {
        const desc = document.getElementById('boardDescription');
        const btn  = document.getElementById('btnDescToggle');
        if (!desc || !btn) { return; }

        desc.classList.add('page-meta--clamp');

        if (desc.scrollHeight > desc.clientHeight) {
            btn.style.display = '';
        }

        btn.addEventListener('click', () => {
            const clamped = desc.classList.toggle('page-meta--clamp');
            btn.textContent = clamped ? '자세히 보기' : '접기';
        });
    }

    static #showErrorToast() {
        const toast = document.getElementById('errorToast');
        if (!toast || !toast.textContent.trim()) { return; }
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }
}

document.addEventListener('DOMContentLoaded', () => Layout.init());
