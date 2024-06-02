package github.com.jbabe.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFile;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFileJpa;
import github.com.jbabe.repository.competitionImg.CompetitionImgJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.repository.gallery.GalleryJpa;
import github.com.jbabe.repository.galleryImg.GalleryImgJpa;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFileJpa;
import github.com.jbabe.repository.postImg.PostImgJpa;
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

    private final CompetitionAttachedFileJpa competitionAttachedFileJpa;
    private final CompetitionImgJpa competitionImgJpa;
    private final CompetitionRecordJpa competitionRecordJpa;
    private final GalleryImgJpa galleryImgJpa;
    private final PostImgJpa postImgJpa;
    private final PostAttachedFileJpa postAttachedFileJpa;


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

    public Map<String, Object> ckEditorImgUpload(MultipartFile file){
        Map<String, Object> response = new HashMap<>();

        PutObjectRequest putObjectRequest = makePutObjectRequest(file);
        amazonS3Client.putObject(putObjectRequest);
        String url = amazonS3Client.getUrl(bucketName, putObjectRequest.getKey()).toString();
        response.put("uploaded", true);
        response.put("url", url);

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

    public List<String> cleanupS3Bucket() {
        List<String> byDB = getAllFileKeysByDB();
        List<String> byBucket = getAllFileKeysByBucket();
        byBucket.removeAll(byDB);
        if(byBucket.isEmpty()) return null;
        try{
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                    .withKeys(byBucket.toArray(new String[0]));
            amazonS3Client.deleteObjects(deleteObjectsRequest);

//            for(String removeKey :byBucket)
//                amazonS3Client.deleteObject(bucketName, removeKey);


            return byBucket;

        }catch (AmazonS3Exception e){
            e.printStackTrace();
            throw new StorageUpdateFailedException("Bucket Cleanup Failed "+e.getMessage(), "failed");
        }
    }

    public List<String> getAllFileKeysByBucket(){
        List<String> fileKeys = new ArrayList<>();
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucketName);
        ListObjectsV2Result result;
        do{

            result = amazonS3Client.listObjectsV2
                    (new ListObjectsV2Request().withBucketName(bucketName));

            for(S3ObjectSummary objectSummary : result.getObjectSummaries()){
                String objectKey = objectSummary.getKey();
                fileKeys.add(objectKey);
            }
            request.setContinuationToken(result.getNextContinuationToken());
        }while (result.isTruncated());

        return fileKeys;
    }



    public List<String> getAllFileKeysByDB(){
        Set<String> filePath = new HashSet<>();
        filePath.addAll(competitionAttachedFileJpa.findAllFilePath());
        filePath.addAll(competitionImgJpa.findAllFilePath());
        filePath.addAll(competitionRecordJpa.findAllFilePath());
        filePath.addAll(galleryImgJpa.findAllFilePath());
        filePath.addAll(postAttachedFileJpa.findAllFilePath());
        filePath.addAll(postImgJpa.findAllFilePath());

        List<String> uniqueFilePath = new ArrayList<>(filePath);

        for(int i = 0; i<filePath.size(); i++){
            String[] parts = uniqueFilePath.get(i).split("/");
            String key = parts[parts.length - 1];
            uniqueFilePath.set(i, key);
        }
        return uniqueFilePath;
    }
}
