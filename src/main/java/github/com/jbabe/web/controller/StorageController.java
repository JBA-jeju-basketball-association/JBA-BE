package github.com.jbabe.web.controller;

import github.com.jbabe.service.exception.StorageUpdateFailedException;
import github.com.jbabe.service.storage.ServerDiskService;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/storage")
@RequiredArgsConstructor
public class StorageController implements StorageControllerDocs{
    private final StorageService storageService;
    private final ServerDiskService serverDiskService;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Override
    @PostMapping(value = "/multipart-files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto uploadMultipleFiles(//여러개 업로드
                                           @RequestPart("uploadFiles") List<MultipartFile> multipartFiles,
                                           @RequestParam(required = false) Optional<SaveFileType> type
    ) {
        List<FileDto> response;
        if(activeProfile.equals("dev"))
            response = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(()->SaveFileType.small));
        else if (activeProfile.equals("default"))
            response = serverDiskService.fileUploadAndGetUrl(multipartFiles);
        else throw new StorageUpdateFailedException("activeProfile is not valid", activeProfile);

        return new ResponseDto(response);
    }

    @PostMapping("/ck-editor-upload")
    public Map<String, Object> ckEditorImgUpload(//한개 업로드
                                  @RequestPart("uploadFile") MultipartFile multipartFile
    ) {
        return storageService.ckEditorImgUpload(multipartFile);
    }

    @DeleteMapping("/multipart-files")//업로드 취소 (삭제)
    public ResponseDto deleteMultipleFiles(@RequestParam(value = "file-url") List<String> fileUrls ) {
        storageService.uploadCancel(fileUrls);
        return new ResponseDto();
    }
    @PutMapping(value = "/multipart-files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto modifyMultipleFiles(@RequestParam(value = "file-url") List<String> deleteFileUrls,
                                           @RequestPart("uploadFiles") List<MultipartFile> multipartFiles,
                                           @RequestParam(required = false) Optional<SaveFileType> type){

        List<FileDto> response = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(()->SaveFileType.small));
        storageService.uploadCancel(deleteFileUrls);
        return new ResponseDto(response);
    }

}
