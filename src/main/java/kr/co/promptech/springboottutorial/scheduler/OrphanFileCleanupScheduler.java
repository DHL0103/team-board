package kr.co.promptech.springboottutorial.scheduler;

import kr.co.promptech.springboottutorial.mapper.PostFileMapper;
import kr.co.promptech.springboottutorial.model.PostFile;
import kr.co.promptech.springboottutorial.service.PostFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrphanFileCleanupScheduler {

    private final PostFileMapper postFileMapper;
    private final PostFileService postFileService;

    @Scheduled(cron = "${app.cleanup.cron}")
    public void cleanUpOrphanInlineImages() {
        List<PostFile> orphans = postFileMapper.findOrphanInlineImages();
        log.info("고아 인라인 이미지 정리 실행 — 대상 {}개", orphans.size());
        if (orphans.isEmpty()) {
            return;
        }
        List<Long> ids = orphans.stream().map(PostFile::getId).toList();
        postFileService.deleteFiles(ids);
        log.info("고아 인라인 이미지 {}개 정리 완료", orphans.size());
    }
}