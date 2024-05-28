package github.com.jbabe.config.customEventListener;

import github.com.jbabe.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
    private final StorageService storageService;
    @Scheduled(cron = "1 0 0 * * MON")
    public void cleanupOldWithdrawnUserAndSetupOldProduct(){
        log.warn("파일 삭제 시작");
        log.info("삭제된 파일 목록 : "+storageService.cleanupS3Bucket());
        log.info("미사용 첨부파일을 제거 하였습니다.");
    }
}
