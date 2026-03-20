package kr.co.promptech.springboottutorial.exception;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(Long id) {
        super("Board not found: " + id);
    }
}
