package kr.co.promptech.springboottutorial.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {

    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("s", "u")
            .addAttributes("img", "src", "alt", "width", "height");

    public String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        // base URI 제공 → /files/uuid가 https://placeholder.local/files/uuid로 절대화되어 프로토콜 체크 통과
        // jsoup이 출력도 절대경로로 변환하므로 후처리로 다시 상대경로로 복원
        String cleaned = Jsoup.clean(html, "https://placeholder.local", SAFELIST);
        return cleaned.replace("https://placeholder.local/files/", "/files/");
    }
}
