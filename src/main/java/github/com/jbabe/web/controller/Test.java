package github.com.jbabe.web.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.advice.errorResponseDto.ErrorResponse;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest.FinishUploadRequest;
import github.com.jbabe.web.dto.awsTest.PreSignedUrlAbortRequest;
import github.com.jbabe.web.dto.awsTest.PreSignedUrlCreateRequest;
import github.com.jbabe.web.dto.awsTest.PreSignedUploadInitiateRequest;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static io.lettuce.core.LettuceFutures.awaitAll;
import static org.springframework.web.servlet.function.ServerResponse.async;

@RestController
@RequiredArgsConstructor
public class Test {

    private final UserJpa userJpa;
    private final RedisTokenRepository redisTokenRepository;
    private final AmazonS3 amazonS3Client;
    private final StorageService storageService;

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(()-> new NotFoundException("Not Found User", ""));
        return user.getName();
    }

    @GetMapping("/testSihu")
    public Object test() {
        return storageService.cleanupS3Bucket();
    }

    @PostMapping("/logoutTest")
    public String logoutTest(HttpServletRequest httpServletRequest){
        Set<String> oldToken = new HashSet<>();
        oldToken.add(httpServletRequest.getParameter("access"));
        oldToken.add(httpServletRequest.getParameter("refresh"));
        redisTokenRepository.addBlacklistToken(httpServletRequest.getParameter("email"), oldToken, Duration.ofMinutes(1));
        return "성공";
    }


    /*
    우아한 기술 블로그 참고 https://techblog.woowahan.com/11392/
    aws 멀티파트 , 또는 spring 멀티파트로 s3 버킷 사용⬇️
     */
    @PostMapping("/initiate-upload")//업로드시작
    public InitiateMultipartUploadResult initiateUpload(@RequestBody PreSignedUploadInitiateRequest request) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.getFileSize());
        objectMetadata.setContentType(URLConnection.guessContentTypeFromName(request.getFileType()));

        InitiateMultipartUploadRequest initiateRequest = new InitiateMultipartUploadRequest("bucketName", "objectName", objectMetadata);
        return amazonS3Client.initiateMultipartUpload(initiateRequest);
    }

    @PostMapping("/presigned-url") //presigned 발급
    public URL initiateUpload(@RequestBody PreSignedUrlCreateRequest request) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        Date expiration = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest("bucketName", "objectName")
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        generatePresignedUrlRequest.addRequestParameter("uploadId", request.getUploadId());
        generatePresignedUrlRequest.addRequestParameter("partNumber", String.valueOf(request.getPartNumber()));

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
    @PostMapping("/complete-upload")//업로드완료
    public CompleteMultipartUploadResult completeUpload(@RequestBody FinishUploadRequest finishUploadRequest) {
        List<PartETag> partETags = finishUploadRequest.getParts().stream()
                .map(part -> new PartETag(part.getPartNumber(), part.getETag()))
                .collect(Collectors.toList());

        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
                "bucketName",
                "objectName",
                finishUploadRequest.getUploadId(),
                partETags
        );

        return amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }
    @PostMapping("/abort-upload")//업로드 취소
    public void initiateUpload(@RequestBody PreSignedUrlAbortRequest request) {
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest("bucketName", "objectName", request.getUploadId());
        amazonS3Client.abortMultipartUpload(abortMultipartUploadRequest);
    }
    //⬆️aws sdk 사용
    //⬇️멀티파트 방식
//    @PostMapping("/multipart-files")//단일호출 한개 업로드
//    public void uploadMultipleFile(@RequestPart MultipartFile file) throws IOException {
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType(file.getContentType());
//        objectMetadata.setContentLength(file.getSize());
//
//        PutObjectRequest putObjectRequest = new PutObjectRequest(
//                "bucketName",
//                "objectKey",
//                file.getInputStream(),
//                objectMetadata
//        );
//
//        amazonS3Client.putObject(putObjectRequest);
//    }

}
