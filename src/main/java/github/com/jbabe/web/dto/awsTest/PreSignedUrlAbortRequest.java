package github.com.jbabe.web.dto.awsTest;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PreSignedUrlAbortRequest {
    private String uploadId;
}
