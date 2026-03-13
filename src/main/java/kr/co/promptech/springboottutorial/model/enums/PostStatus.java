package kr.co.promptech.springboottutorial.model.enums;

public enum PostStatus {
    PROGRESS("진행 중"),
    REQUESTED("승인 요청"),
    APPROVED("완료"),
    REJECTED("반려");

    private final String label;

    PostStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
