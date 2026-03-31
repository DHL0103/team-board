package kr.co.promptech.springboottutorial.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDto {
    Long boardId;
    @NotBlank
    @Size(max = 100)
    String title;
    @NotBlank
    @Size(max = 10000)
    String content;
    String dueDate;
    List<MultipartFile> files;
    List<Long> assigneeIds;
}
