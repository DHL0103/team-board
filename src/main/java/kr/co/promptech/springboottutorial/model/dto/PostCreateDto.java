package kr.co.promptech.springboottutorial.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostCreateDto {
    Long boardId;
    String title;
    String content;
    LocalDateTime dueDate;
    List<MultipartFile> files;
}
