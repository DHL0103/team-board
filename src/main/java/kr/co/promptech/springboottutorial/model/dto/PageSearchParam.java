package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageSearchParam {
    private int page;
    private int size;
    private String sort;

    public int getSize() {
        return size <= 0 ? 10 : size;
    }

    public int getOffset() {
        return page * getSize();
    }
}
