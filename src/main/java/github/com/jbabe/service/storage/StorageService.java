package github.com.jbabe.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import github.com.jbabe.service.exception.CustomBadCredentialsException;
import github.com.jbabe.service.exception.FileUploadFailedException;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;

    public List<String> fileUploadAndGetUrl(List<MultipartFile> multipartFiles, SaveFileType type){
        List<String> fileUrls = new ArrayList<>();
        switch (type){
            case small:
                for(MultipartFile file : multipartFiles){
                    PutObjectRequest putObjectRequest = makePutObjectRequest(file);
                    amazonS3Client.putObject(putObjectRequest);
                    fileUrls.add(amazonS3Client.getUrl(bucketName,putObjectRequest.getKey()).toString());
                }
                break;
            case large:
                //TODO: 추후 하이라이트 영상 같은 이미지에 비해 고용량 업로드가 필요할시
                // 로딩바 구현을위해 aws s3 로 구현
                break;
        }
        return fileUrls;
    }

    private PutObjectRequest makePutObjectRequest(MultipartFile file) {
        String storageFileName = makeStorageFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            return new PutObjectRequest(bucketName, storageFileName, file.getInputStream(), objectMetadata);
        }catch (IOException ioException){
            throw new FileUploadFailedException("File Upload Failed", file.getOriginalFilename());
        }


    }

    public String makeStorageFileName(String originalFileName){
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        return UUID.randomUUID() + "." + extension;
    }
}
