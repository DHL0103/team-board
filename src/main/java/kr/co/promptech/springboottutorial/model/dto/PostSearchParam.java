package kr.co.promptech.springboottutorial.model.dto;

import lombok.Getter;

@Getter
public class PostSearchParam extends PageSearchParam {
    private final String postTitle;
    private final String postStatus;
    private final Boolean mineOnly;

    public PostSearchParam(int page, int size, String sort,
                           String postTitle, String postStatus, Boolean mineOnly) {
        super(page, size, sort);
        this.postTitle = postTitle;
        this.postStatus = postStatus;
        this.mineOnly = mineOnly;
    }
}
