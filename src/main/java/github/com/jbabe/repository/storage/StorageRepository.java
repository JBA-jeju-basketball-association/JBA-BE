//package github.com.jbabe.repository.storage;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@RequiredArgsConstructor
//public class StorageRepository {
//    private ObjectMetadata objectMetadata = new ObjectMetadata();
//
//    @PostConstruct
//    public void setObjectMetadata(){
//
//        objectMetadata.setContentType(file.getContentType());
//        objectMetadata.setContentLength(file.getInputStream().available());
//    }
//
//
//}
