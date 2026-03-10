// ── 토스트 유틸 ──
function showToast(message) {
    const toast = document.getElementById("errorToast");
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 3000);
}

// ── 마감일 칩 유틸 ──
function initDueChips() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    document.querySelectorAll(".due-chip").forEach(chip => {
        const due = chip.dataset.due;
        if (!due) { chip.classList.add("none"); return; }
        const dueDate = new Date(due);
        const diffDays = Math.floor((dueDate - today) / (1000 * 60 * 60 * 24));
        const label = chip.querySelector(".due-label");
        if (diffDays < 0)        { chip.classList.add("over"); label.textContent += " 초과"; }
        else if (diffDays === 0) { chip.classList.add("warn"); label.textContent = "오늘 마감"; }
        else if (diffDays === 1) { chip.classList.add("warn"); label.textContent = "내일 마감"; }
        else if (diffDays <= 3)  { chip.classList.add("warn"); label.textContent += " 마감"; }
        else                     { chip.classList.add("safe"); label.textContent += " 마감"; }
    });
}

initDueChips();

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
