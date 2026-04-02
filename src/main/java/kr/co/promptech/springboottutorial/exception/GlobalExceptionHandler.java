package kr.co.promptech.springboottutorial.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static String refererPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return null;
        }
        try {
            return new URI(referer).getRawPath();
        } catch (Exception e) {
            return null;
        }
    }

    @ExceptionHandler({PostNotFoundException.class, BoardNotFoundException.class})
    public String handleNotFound() {
        return "error/404";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolation(HttpServletRequest request) {
        String path = refererPath(request);
        if (path != null) {
            return "redirect:" + path + "?error=validation";
        }
        return "redirect:/boards?error=validation";
    }

    @ExceptionHandler(BindException.class)
    public String handleValidation(HttpServletRequest request) {
        String path = refererPath(request);
        if (path != null) {
            return "redirect:" + path + "?error=validation";
        }
        return "redirect:/boards?error=validation";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(HttpServletRequest request) {
        String path = refererPath(request);
        if (path != null) {
            return "redirect:" + path + "?error=fileSize";
        }
        return "redirect:/boards?error=fileSize";
    }

    @ExceptionHandler({DuplicateKeyException.class, IllegalStateException.class})
    public String handleDuplicate(HttpServletRequest request) {
        String path = refererPath(request);
        if (path != null) {
            return "redirect:" + path;
        }
        return "redirect:/boards";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected() {
        return "error/500";
    }
}
