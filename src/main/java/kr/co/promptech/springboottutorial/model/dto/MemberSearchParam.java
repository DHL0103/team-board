package kr.co.promptech.springboottutorial.model.dto;

import lombok.Getter;

@Getter
public class MemberSearchParam extends PageSearchParam {
    private final String username;
    private final String filter;

    public MemberSearchParam(int page, int size, String sort,
                             String username, String filter) {
        super(page, size, sort);
        this.username = username;
        this.filter = filter;
    }
}
