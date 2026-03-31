class Modal {

    static #initialized = false;

    static open(id) {
        document.getElementById(id).classList.add('open');
        document.body.style.overflow = 'hidden';
    }

    static close(id) {
        document.getElementById(id).classList.remove('open');
        document.body.style.overflow = '';
    }

    static init() {
        if (Modal.#initialized) { return; }
        Modal.#initialized = true;

        document.querySelectorAll('.modal-overlay').forEach(overlay => {
            overlay.addEventListener('click', e => {
                if (e.target === overlay) { Modal.close(overlay.id); }
            });
        });
        document.querySelectorAll('.modal-close').forEach(btn => {
            btn.addEventListener('click', () => {
                const overlay = btn.closest('.modal-overlay');
                if (overlay) { Modal.close(overlay.id); }
            });
        });
        document.addEventListener('keydown', e => {
            if (e.key === 'Escape') {
                document.querySelectorAll('.modal-overlay.open').forEach(m => Modal.close(m.id));
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => Modal.init());
