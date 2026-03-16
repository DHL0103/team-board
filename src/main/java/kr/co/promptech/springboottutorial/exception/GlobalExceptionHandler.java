package kr.co.promptech.springboottutorial.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            String separator = referer.contains("?") ? "&" : "?";
            return "redirect:" + referer + separator + "error=fileSize";
        }
        return "redirect:/board?error=fileSize";
    }

    @ExceptionHandler({DuplicateKeyException.class, IllegalStateException.class})
    public String handleDuplicate(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/board";
    }
}
