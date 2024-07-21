package github.com.jbabe.service.storage;

import github.com.jbabe.web.dto.storage.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ServerDiskService {
    @Value("${upload.path}")
    private String uploadPath;

    public String fileUploadAndGetUrl(List<MultipartFile> multipartFiles) {

        multipartFiles.forEach(multipartFile ->
                {
                    // 2. 서버에 파일 저장 & DB에 파일 정보(fileinfo) 저장
                    // - 동일 파일명을 피하기 위해 random값 사용
                    String originalFilename = multipartFile.getOriginalFilename();
                    String saveFileName = createSaveFileName(originalFilename);

                    // 2-1.서버에 파일 저장
                    try {
                        multipartFile.transferTo(new File(getFullPath(saveFileName)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        return "OK";
    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }else {
                throw new FileNotFoundException("File not found: " + fileName);
            }
        }catch (MalformedURLException | FileNotFoundException ex) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }


    // 파일 저장 이름 만들기
// - 사용자들이 올리는 파일 이름이 같을 수 있으므로, 자체적으로 랜덤 이름을 만들어 사용한다
    private String createSaveFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자명 구하기
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // fullPath 만들기
    private String getFullPath(String filename) {
        return "/mnt/files/" + filename;
    }
}
