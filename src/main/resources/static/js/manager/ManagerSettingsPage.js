class ManagerSettingsPage {

    static init() {
        new CharCounter(document.getElementById('settingsName'), document.getElementById('settingsNameCount'), 50, 45);
        new CharCounter(document.getElementById('settingsDescription'), document.getElementById('settingsDescCount'), 500, 450);
    }
}

document.addEventListener('DOMContentLoaded', () => ManagerSettingsPage.init());
