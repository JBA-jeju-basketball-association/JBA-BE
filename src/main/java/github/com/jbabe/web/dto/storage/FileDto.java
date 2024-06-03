package github.com.jbabe.web.dto.storage;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileDto {
    @Schema(description = "첨부파일 원본 파일명", example = "코딩과 탈모의 인과관계.jpg")
    private String fileName;
    @JsonAlias({"fileUrl", "imgUrl"})
    @Schema(description = "첨부파일 URL", example = "https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png")
    private String fileUrl;
}
