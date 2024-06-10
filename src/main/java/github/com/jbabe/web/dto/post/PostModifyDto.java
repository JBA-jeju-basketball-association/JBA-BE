package github.com.jbabe.web.dto.post;

import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PostModifyDto {
    @NotNull(message = "제목은 필수입니다.")
    @Schema(description = "제목", example = "게시물 입니다.")
    private String title;
    @NotNull(message = "내용은 필수입니다.")
    @Schema(description = "응답 메세지", example = "<p>POST 페이지 더미데이터입니다.</p>")
    private String content;
    @Schema(description = "머릿말", example = "합격자 발표")
    private String foreword;


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
    private List<FileDto> remainingFiles;
    @Schema(description = "원 게시물의 이미지 정보", example = "[\n" +
            "    {\n" +
            "        \"fileName\": \"1111.png\",\n" +
            "        \"imgUrl\": \"1111.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"2222.png\",\n" +
            "        \"imgUrl\": \"2222.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"3333.png\",\n" +
            "        \"imgUrl\": \"3333.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"4444.png\",\n" +
            "        \"imgUrl\": \"4444.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"5555.png\",\n" +
            "        \"imgUrl\": \"5555.url\"\n" +
            "    }\n" +
            "]")
    private List<FileDto> postImgs;
}
