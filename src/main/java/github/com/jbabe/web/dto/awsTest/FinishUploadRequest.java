package github.com.jbabe.web.dto.awsTest;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class FinishUploadRequest {
    private String uploadId;
    private List<Part> parts;

    @Getter
    @NoArgsConstructor
    public static class Part {
        private int partNumber;
        private String eTag;
    }
}
