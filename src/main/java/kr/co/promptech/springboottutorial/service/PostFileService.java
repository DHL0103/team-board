package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostFileMapper;
import kr.co.promptech.springboottutorial.model.PostFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostFileService {
    private final PostFileMapper postFileMapper;
    private String uploadDir = "uploads";

    public void saveFile(MultipartFile file, Long postId){
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + ext;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path path = uploadPath.resolve(savedName);
            Files.copy(file.getInputStream(), path);

            PostFile postFile = new PostFile();
            postFile.setPostId(postId);
            postFile.setOriginalName(originalName);
            postFile.setStoredPath(savedName);
            postFile.setFileSize(file.getSize());
            postFile.setCreatedAt(LocalDateTime.now());

            postFileMapper.saveFile(postFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
