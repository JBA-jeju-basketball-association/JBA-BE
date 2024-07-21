package github.com.jbabe.web.controller;

import github.com.jbabe.service.storage.ServerDiskService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.storage.FileDto;
import github.com.jbabe.web.dto.storage.RequestFileDto;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/upload")
@RequiredArgsConstructor
public class ServerDiskController {

    private final ServerDiskService serverDiskService;

    @PostMapping("/uploadFiles")
    public ResponseDto uploadFile(@RequestPart("uploadFiles")List<MultipartFile> multipartFiles) {
        List<RequestFileDto> response = serverDiskService.fileUploadAndGetUrl(multipartFiles);
        return new ResponseDto(response);
    }

    @GetMapping("/getFile/{fileServerName}")
    public ResponseEntity<Resource> getFiles(@PathVariable("fileServerName") String fileServerName) {
        try {
            Resource resource = serverDiskService.loadFileAsResource(fileServerName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
