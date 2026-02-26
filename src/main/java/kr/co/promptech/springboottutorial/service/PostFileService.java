package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostFileMapper;
import kr.co.promptech.springboottutorial.model.PostFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostFileService {
    private final PostFileMapper postFileMapper;
    private String uploadDir = "uploads";

    public List<PostFile> getFilesByPostId(Long postId) {
        return postFileMapper.findByPostId(postId);
    }

    public void deleteFiles(List<Long> ids) {
        for (Long id : ids) {
            PostFile postFile = postFileMapper.findById(id);
            if (postFile == null) continue;

            try {
                Files.deleteIfExists(Paths.get(uploadDir).resolve(postFile.getStoredPath()));
            } catch (IOException e) {
                log.warn("파일 삭제 실패: {}", postFile.getStoredPath(), e);
            }

            postFileMapper.deleteById(id);
        }
    }

    public void deleteFilesByPostId(Long postId) {
        List<PostFile> files = postFileMapper.findByPostId(postId);
        List<Long> ids = files.stream().map(PostFile::getId).toList();
        if (!ids.isEmpty()) {
            deleteFiles(ids);
        }
    }

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
