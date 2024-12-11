package github.com.jbabe.web.dto.storage;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "첨부파일 ID", example = "1")
    private Integer fileId;

    @NotBlank(message = "파일명은 필수입니다.")
    @Schema(description = "첨부파일 원본 파일명", example = "코딩과 탈모의 인과관계.jpg")
    private String fileName;

    @NotBlank(message = "URL은 필수입니다.")
    @JsonAlias({"fileUrl", "imgUrl"})
    @Schema(description = "첨부파일 URL", example = "https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png")
    private String fileUrl;

    public FileDto(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
