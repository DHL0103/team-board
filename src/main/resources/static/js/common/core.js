// ── App 네임스페이스 생성 ──
window.App = {};

// ── 햄버거 메뉴 ──
(function () {
    const hamburger = document.getElementById('hamburger');
    const navMenu   = document.getElementById('nav-menu');
    if (!hamburger || !navMenu) { return; }

    hamburger.addEventListener('click', function () {
        navMenu.classList.toggle('open');
    });

    document.addEventListener('click', function (e) {
        if (!hamburger.contains(e.target) && !navMenu.contains(e.target)) {
            navMenu.classList.remove('open');
        }
    });
})();

// ── 보드 설명 접기/펼치기 ──
(function () {
    var desc = document.getElementById("boardDescription");
    var btn  = document.getElementById("btnDescToggle");
    if (!desc || !btn) { return; }

    desc.classList.add("page-meta--clamp");

    if (desc.scrollHeight > desc.clientHeight) {
        btn.style.display = "";
    }

    btn.addEventListener("click", function () {
        var clamped = desc.classList.toggle("page-meta--clamp");
        btn.textContent = clamped ? "자세히 보기" : "접기";
    });
})();

// ── 에러 토스트 자동 표시 ──
window.addEventListener('load', function () {
    const toast = document.getElementById('errorToast');
    if (toast && toast.textContent.trim()) {
        toast.classList.add('show');
        setTimeout(function () { toast.classList.remove('show'); }, 3000);
    }
});
