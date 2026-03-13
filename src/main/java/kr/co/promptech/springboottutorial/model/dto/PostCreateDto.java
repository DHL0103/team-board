package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostCreateDto {
    Long boardId;
    String title;
    String content;
    String dueDate;
    List<MultipartFile> files;
    List<Long> assigneeIds;
}
