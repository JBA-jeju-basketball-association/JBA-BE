package github.com.jbabe.web.controller;

import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/storage")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;
    @PostMapping("/multipart-files")
    public ResponseDto uploadMultipleFiles(//여러개 업로드
                                           @RequestPart("uploadFiles") List<MultipartFile> multipartFiles,
                                           @RequestParam(required = false) Optional<SaveFileType> type
    ) {
        List<String> fileUrls = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(()->SaveFileType.small));
        return new ResponseDto(fileUrls);
    }

    @DeleteMapping("/multipart-files")//업로드 취소 (삭제)
    public ResponseDto deleteMultipleFiles(@RequestParam(value = "file-url") List<String> fileUrls ) {
        storageService.uploadCancel(fileUrls);
        return new ResponseDto();
    }
    @PutMapping("/multipart-files")
    public ResponseDto modifyMultipleFiles(@RequestParam(value = "file-url") List<String> deleteFileUrls,
                                           @RequestPart("uploadFiles") List<MultipartFile> multipartFiles,
                                           @RequestParam(required = false) Optional<SaveFileType> type){

        List<String> fileUrls = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(()->SaveFileType.small));
        storageService.uploadCancel(deleteFileUrls);
        return new ResponseDto(fileUrls);
    }

}
