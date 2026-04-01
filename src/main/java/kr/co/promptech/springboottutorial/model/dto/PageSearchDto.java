package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PageSearchDto {
    private String q = "";
    private String status = "";
    private String sort = "";
    private int page;
    private int size = 10;

    public int getOffset() {
        return page * size;
    }
}
