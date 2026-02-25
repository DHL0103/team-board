// ── 토스트 유틸 ──
function showToast(message) {
    const toast = document.getElementById("errorToast");
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 3000);
}

// ── 회원 이름 캐싱 및 조회 유틸 ──
const memberCache = {};

async function fetchUsername(memberId) {
    if (!memberId) return "알 수 없음";
    if (memberCache[memberId]) return memberCache[memberId];

    try {
        const res = await fetch(`/member/${memberId}`);
        if (!res.ok) throw new Error("Network response was not ok");
        const member = await res.json();
        memberCache[memberId] = member.username;
        return member.username;
    } catch {
        return "알 수 없음";
    }
}
