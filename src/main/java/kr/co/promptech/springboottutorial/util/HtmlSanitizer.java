package kr.co.promptech.springboottutorial.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {

    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("s", "u")
            .addAttributes("img", "src", "alt", "width", "height");

    /**
     * 에디터 본문 HTML을 화이트리스트 기반으로 정화한다.
     * Jsoup이 상대 경로를 절대 경로로 변환하므로, 후처리로 /api/files/ 및 이전 /files/ 경로를 상대 경로로 복원한다.
     * @param html 정화할 원본 HTML
     * @return 정화된 HTML (null 또는 공백 입력 시 빈 문자열)
     */
    public String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        // base URI 제공 → /api/files/uuid가 https://placeholder.local/api/files/uuid로 절대화되어 프로토콜 체크 통과
        // jsoup이 출력도 절대경로로 변환하므로 후처리로 다시 상대경로로 복원
        String cleaned = Jsoup.clean(html, "https://placeholder.local", SAFELIST);
        return cleaned
                .replace("https://placeholder.local/api/files/", "/api/files/")
                .replace("https://placeholder.local/files/", "/api/files/");
    }
}
