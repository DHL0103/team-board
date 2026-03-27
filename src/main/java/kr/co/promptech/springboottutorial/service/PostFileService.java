package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostFileMapper;
import kr.co.promptech.springboottutorial.model.PostFile;
import kr.co.promptech.springboottutorial.model.dto.FileDownloadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostFileService {
    private final PostFileMapper postFileMapper;
    @Value("${app.upload-dir}")
    private String uploadDir;

    public List<PostFile> getFilesByPostId(Long postId) {
        return postFileMapper.findByPostId(postId);
    }

    public FileDownloadDto getFileForDownload(String storedPath, String displayName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(storedPath).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return null;
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String name = (displayName != null && !displayName.isBlank()) ? displayName : storedPath;
        return new FileDownloadDto(resource, contentType, name);
    }

    public void deleteFiles(List<Long> ids) {
        if (ids == null) {
            return;
        }
        for (Long id : ids) {
            PostFile postFile = postFileMapper.findById(id);
            if (postFile == null) {
                continue;
            }
            try {
                Files.deleteIfExists(Paths.get(uploadDir).resolve(postFile.getStoredPath()));
            } catch (IOException e) {
                log.warn("파일 삭제 실패: {}", postFile.getStoredPath(), e);
            }

            postFileMapper.deleteById(id);
        }
    }

    public void deleteFilesByPostId(Long postId) {
        List<PostFile> files = postFileMapper.findAllByPostId(postId);
        List<Long> ids = files.stream().map(PostFile::getId).toList();
        if (!ids.isEmpty()) {
            deleteFiles(ids);
        }
    }

    public void saveFiles(List<MultipartFile> files, Long postId) {
        if (files == null) {
            return;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                saveFile(file, postId);
            } catch (Exception e) {
                log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
            }
        }
    }

    private static final Pattern INLINE_IMG_PATTERN = Pattern.compile("/files/([^\"'\\s]+)");

    private List<String> extractStoredPaths(String content) {
        List<String> paths = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return paths;
        }
        Matcher matcher = INLINE_IMG_PATTERN.matcher(content);
        while (matcher.find()) {
            paths.add(matcher.group(1));
        }
        return paths;
    }

    public void linkInlineImages(String content, Long postId) {
        List<String> paths = extractStoredPaths(content);
        if (paths.isEmpty()) {
            return;
        }
        postFileMapper.linkInlineImages(paths, postId);
    }

    public void syncInlineImages(String oldContent, String newContent, Long postId) {
        List<String> oldPaths = extractStoredPaths(oldContent);
        List<String> newPaths = extractStoredPaths(newContent);

        List<String> removed = new ArrayList<>(oldPaths);
        removed.removeAll(newPaths);
        if (!removed.isEmpty()) {
            List<PostFile> toDelete = postFileMapper.findByStoredPaths(removed);
            List<Long> ids = toDelete.stream().map(PostFile::getId).toList();
            for (PostFile file : toDelete) {
                try {
                    Files.deleteIfExists(Paths.get(uploadDir).resolve(file.getStoredPath()));
                } catch (IOException e) {
                    log.warn("인라인 이미지 삭제 실패: {}", file.getStoredPath(), e);
                }
            }
            if (!ids.isEmpty()) {
                postFileMapper.deleteByIds(ids);
            }
        }

        List<String> added = new ArrayList<>(newPaths);
        added.removeAll(oldPaths);
        if (!added.isEmpty()) {
            postFileMapper.linkInlineImages(added, postId);
        }
    }

    public String saveInlineImage(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String savedName = UUID.randomUUID() + ext;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(savedName));

            PostFile postFile = PostFile.builder()
                    .postId(null)
                    .originalName(originalName)
                    .storedPath(savedName)
                    .fileSize(file.getSize())
                    .inline(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            postFileMapper.saveFile(postFile);

            return "/files/" + savedName;
        } catch (IOException e) {
            throw new RuntimeException("인라인 이미지 저장 실패", e);
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

            PostFile postFile = PostFile.builder()
                    .postId(postId)
                    .originalName(originalName)
                    .storedPath(savedName)
                    .fileSize(file.getSize())
                    .inline(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            postFileMapper.saveFile(postFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
