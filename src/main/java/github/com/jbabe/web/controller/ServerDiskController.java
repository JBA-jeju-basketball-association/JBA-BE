package github.com.jbabe.web.controller;

import github.com.jbabe.service.storage.ServerDiskService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/api/upload")
@RequiredArgsConstructor
public class ServerDiskController {

    private final ServerDiskService serverDiskService;

    @PostMapping("test")
    public ResponseDto uploadFile(@RequestPart("uploadFiles")List<MultipartFile> multipartFiles) {
        String response = serverDiskService.fileUploadAndGetUrl(multipartFiles);
        return new ResponseDto(response);
    }
}
