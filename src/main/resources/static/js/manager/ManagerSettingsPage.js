class ManagerSettingsPage {

    static #initialized = false;

    static init() {
        if (ManagerSettingsPage.#initialized) { return; }
        ManagerSettingsPage.#initialized = true;

        new CharCounter(document.getElementById('settingsName'), document.getElementById('settingsNameCount'), 50, 45);
        new CharCounter(document.getElementById('settingsDescription'), document.getElementById('settingsDescCount'), 500, 450);
    }
}

document.addEventListener('DOMContentLoaded', () => ManagerSettingsPage.init());
