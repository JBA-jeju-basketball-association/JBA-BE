package github.com.jbabe.service.storage;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFileJpa;
import github.com.jbabe.repository.competitionImg.CompetitionImgJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.galleryImg.GalleryImgJpa;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFileJpa;
import github.com.jbabe.repository.postImg.PostImgJpa;
import github.com.jbabe.service.exception.StorageUpdateFailedException;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerDiskService {
    @Value("${upload.path}")
    private String uploadPath;
    private final CompetitionAttachedFileJpa competitionAttachedFileJpa;
    private final CompetitionImgJpa competitionImgJpa;
    private final CompetitionRecordJpa competitionRecordJpa;
    private final GalleryImgJpa galleryImgJpa;
    private final PostAttachedFileJpa postAttachedFileJpa;
    private final PostImgJpa postImgJpa;

    public ServerDiskService(CompetitionAttachedFileJpa competitionAttachedFileJpa,
                             CompetitionImgJpa competitionImgJpa,
                             CompetitionRecordJpa competitionRecordJpa,
                             GalleryImgJpa galleryImgJpa,
                             PostAttachedFileJpa postAttachedFileJpa,
                             PostImgJpa postImgJpa) {
        this.competitionAttachedFileJpa = competitionAttachedFileJpa;
        this.competitionImgJpa = competitionImgJpa;
        this.competitionRecordJpa = competitionRecordJpa;
        this.galleryImgJpa = galleryImgJpa;
        this.postAttachedFileJpa = postAttachedFileJpa;
        this.postImgJpa = postImgJpa;
    }

    public List<FileDto> fileUploadAndGetUrl(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream().map(multipartFile ->
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

                    return FileDto.builder()
                            .fileUrl("https://shinhs010.codns.com/v1/api/upload/getFile/" + saveFileName)
                            .fileName(originalFilename)
                            .build();
                }
        ).collect(Collectors.toList());
    }

    public Map<String, Object> ckEditorImgUpload(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        String originalFilename = file.getOriginalFilename();
        String saveFileName = createSaveFileName(originalFilename);

        // 2-1.서버에 파일 저장
        try {
            file.transferTo(new File(getFullPath(saveFileName)));
            response.put("uploaded", true);
            response.put("url", "https://shinhs010.codns.com/v1/api/upload/getFile/" + saveFileName);
            response.put("fileName", file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public void fileDelete(List<String> fileUrl) {
        fileUrl.forEach(url -> {
            String fileServerName = getFileNameFromUrl(url);
            Path filePath = Paths.get(uploadPath).resolve(fileServerName).normalize();
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<String> cleanupStorage() {
        List<String> byDB = getAllFileKeysByDB();
        List<String> byStorage = getAllFileServerName();
        List<String> deleteList = new ArrayList<>();
        byStorage.forEach(item -> {
            if (!byDB.contains(item)) {
                Path filePath = Paths.get(uploadPath).resolve(item).normalize();
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                deleteList.add(item);
            }
        });
        return deleteList;
    }

    public Resource loadFileAsResource(String fileServerName) throws FileNotFoundException {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileServerName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }else {
                throw new FileNotFoundException("File not found: " + fileServerName);
            }
        }catch (MalformedURLException | FileNotFoundException ex) {
            throw new FileNotFoundException("File not found: " + fileServerName);
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

    // url 에서 파일명 추출
    public static String getFileNameFromUrl(String url) {
        // 마지막 '/'의 인덱스를 찾음
        int lastSlashIndex = url.lastIndexOf('/');

        // 파일명을 추출하여 반환
        return url.substring(lastSlashIndex + 1);
    }


    public List<String> getAllFileKeysByDB(){
        List<String> filePath = new ArrayList<>();
        filePath.addAll(competitionAttachedFileJpa.findAllFilePath());
        filePath.addAll(competitionImgJpa.findAllFilePath());
        filePath.addAll(competitionRecordJpa.findAllFilePath());
        filePath.addAll(galleryImgJpa.findAllFilePath());
        filePath.addAll(postAttachedFileJpa.findAllFilePath());
        filePath.addAll(postImgJpa.findAllFilePath());

        return filePath.stream().map(ServerDiskService::getFileNameFromUrl).collect(Collectors.toList());
    }

    public List<String> getAllFileServerName() {;
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(uploadPath))){
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    fileNames.add(path.getFileName().toString());
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
}
