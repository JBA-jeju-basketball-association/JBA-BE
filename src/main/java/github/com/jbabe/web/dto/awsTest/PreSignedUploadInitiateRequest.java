package github.com.jbabe.web.dto.awsTest;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PreSignedUploadInitiateRequest {
    private String originalFileName;
    private String fileType;
    private long fileSize;
}

