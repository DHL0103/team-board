package kr.co.promptech.springboottutorial.model.dto;

import lombok.Getter;

@Getter
public class BoardSearchParam extends PageSearchParam {
    private final String boardName;
    private final String boardStatus;

    public BoardSearchParam(int page, int size, String sort,
                            String boardName, String boardStatus) {
        super(page, size, sort);
        this.boardName = boardName;
        this.boardStatus = boardStatus;
    }
}
