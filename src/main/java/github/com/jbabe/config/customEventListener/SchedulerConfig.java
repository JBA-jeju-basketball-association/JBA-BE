package github.com.jbabe.config.customEventListener;

import github.com.jbabe.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
    private final StorageService storageService;
    @Scheduled(cron = "1 0 0 * * MON")
    public void cleanupOldWithdrawnUserAndSetupOldProduct(){
        log.warn("파일 삭제 시작");
        List<String> cleanupList = storageService.cleanupS3Bucket();
        if(cleanupList == null)
            log.info("제거 할 파일이 없습니다.");
        else {
            log.info("삭제된 파일 목록 : " + cleanupList);
            log.info("미사용 첨부파일을 제거 하였습니다.");
        }
    }
}
