class Toast {

    static show(message) {
        const toast = document.getElementById('errorToast');
        if (!toast) { return; }
        toast.textContent = message;
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }
}
