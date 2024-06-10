package github.com.jbabe.web.dto.storage;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "첨부 파일 정보", example = "[\n" +
        "    {\n" +
        "        \"fileName\": \"1111.png\",\n" +
        "        \"fileUrl\": \"1111.url\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"fileName\": \"2222.png\",\n" +
        "        \"fileUrl\": \"2222.url\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"fileName\": \"3333.png\",\n" +
        "        \"fileUrl\": \"3333.url\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"fileName\": \"4444.png\",\n" +
        "        \"fileUrl\": \"4444.url\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"fileName\": \"5555.png\",\n" +
        "        \"fileUrl\": \"5555.url\"\n" +
        "    }\n" +
        "]")
public class FileDto {
    @Schema(description = "첨부파일 원본 파일명", example = "코딩과 탈모의 인과관계.jpg")
    private String fileName;
    @JsonAlias({"fileUrl", "imgUrl"})
    @Schema(description = "첨부파일 URL", example = "https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png")
    private String fileUrl;
}
