package github.com.jbabe.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.StorageUpdateFailedException;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;

    public List<FileDto> fileUploadAndGetUrl(List<MultipartFile> multipartFiles, SaveFileType type){
        List<FileDto> response = new ArrayList<>();

        switch (type){
            case small:
                for(MultipartFile file : multipartFiles){
                    PutObjectRequest putObjectRequest = makePutObjectRequest(file);
                    amazonS3Client.putObject(putObjectRequest);
                    String url = amazonS3Client.getUrl(bucketName,putObjectRequest.getKey()).toString();
                    response.add(new FileDto(file.getOriginalFilename(), url));
                }
                break;
            case large:
                //TODO: 추후 하이라이트 영상 같은 이미지에 비해 고용량 업로드가 필요할시
                // 로딩바 구현을위해 aws s3 로 구현
                break;
        }
        return response;
    }

    private PutObjectRequest makePutObjectRequest(MultipartFile file) {
        String storageFileName = makeStorageFileName(Objects.requireNonNull(file.getOriginalFilename()));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            return new PutObjectRequest(bucketName, storageFileName, file.getInputStream(), objectMetadata);
        }catch (IOException ioException){
            throw new StorageUpdateFailedException("File Upload Failed", file.getOriginalFilename());
        }


    }

    public String makeStorageFileName(String originalFileName){
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        return UUID.randomUUID() + "." + extension;
    }

    public void uploadCancel(List<String> fileUrls) {
        try{
            for(String url : fileUrls){
                String[] parts = url.split("/");
                String key = parts[parts.length - 1];
                amazonS3Client.deleteObject(bucketName,key);
            }
        }catch (AmazonS3Exception e){
          e.printStackTrace();
          throw new StorageUpdateFailedException("File Delete Failed "+e.getMessage(), fileUrls.toString());
        }
    }
}
