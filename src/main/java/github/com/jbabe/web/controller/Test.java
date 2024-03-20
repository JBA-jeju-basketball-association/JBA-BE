package github.com.jbabe.web.controller;

import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.awsTest.FinishUploadRequest;
import github.com.jbabe.web.dto.awsTest.PreSignedUrlAbortRequest;
import github.com.jbabe.web.dto.awsTest.PreSignedUrlCreateRequest;
import github.com.jbabe.web.dto.awsTest.PreSignedUploadInitiateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class Test {

    private final UserJpa userJpa;
    private final RedisTokenRepository redisTokenRepository;
//    private final AmazonS3 amazonS3Client;


//    public Test(UserJpa userJpa) {
//        this.userJpa = userJpa;
//    }

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(()-> new NotFoundException("Not Found User", ""));
        String userName = user.getName();
        return userName;
    }

    @PostMapping("/logoutTest")
    public String logoutTest(HttpServletRequest httpServletRequest){
        Set<String> oldToken = new HashSet<>();
        oldToken.add(httpServletRequest.getParameter("access"));
        oldToken.add(httpServletRequest.getParameter("refresh"));
        redisTokenRepository.addBlacklistToken(httpServletRequest.getParameter("email"), oldToken, Duration.ofMinutes(1));
        return "성공";
    }

    //참고 https://techblog.woowahan.com/11392/

//    @PostMapping("/initiate-upload")//업로드시작
//    public InitiateMultipartUploadResult initiateUpload(@RequestBody PreSignedUploadInitiateRequest request) {
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(request.getFileSize());
//        objectMetadata.setContentType(URLConnection.guessContentTypeFromName(request.getFileType()));
//
//        InitiateMultipartUploadRequest initiateRequest = new InitiateMultipartUploadRequest("bucketName", "objectName", objectMetadata);
//        return amazonS3Client.initiateMultipartUpload(initiateRequest);
//    }
//
//    @PostMapping("/presigned-url")//presigned 발급
//    public URL initiateUpload(@RequestBody PreSignedUrlCreateRequest request) {
//        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
//        Date expiration = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest("bucketName", "objectName")
//                        .withMethod(HttpMethod.PUT)
//                        .withExpiration(expiration);
//        generatePresignedUrlRequest.addRequestParameter("uploadId", request.getUploadId());
//        generatePresignedUrlRequest.addRequestParameter("partNumber", String.valueOf(request.getPartNumber()));
//
//        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
//    }
//    @PostMapping("/complete-upload")//업로드완료
//    public CompleteMultipartUploadResult completeUpload(@RequestBody FinishUploadRequest finishUploadRequest) {
//        List<PartETag> partETags = finishUploadRequest.getParts().stream()
//                .map(part -> new PartETag(part.getPartNumber(), part.getETag()))
//                .collect(Collectors.toList());
//
//        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
//                "bucketName",
//                "objectName",
//                finishUploadRequest.getUploadId(),
//                partETags
//        );
//
//        return amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
//    }
//    @PostMapping("/abort-upload")//업로드 취소
//    public void initiateUpload(@RequestBody PreSignedUrlAbortRequest request) {
//        AbortMultipartUploadRequest abortMultipartUploadRequest =
//                new AbortMultipartUploadRequest("bucketName", "objectName", request.getUploadId());
//        amazonS3Client.abortMultipartUpload(abortMultipartUploadRequest);
//    }

}
