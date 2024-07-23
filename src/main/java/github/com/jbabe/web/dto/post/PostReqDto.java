package github.com.jbabe.web.dto.post;

import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class PostReqDto {
    @NotBlank(message = "제목은 필수입니다.")
    @Schema(description = "제목", example = "게시물 입니다.")
    private String title;
    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "내용", example = "<p>POST 페이지 더미데이터입니다.</p>")
    private String content;
    @Schema(description = "원 게시물의 이미지 정보", example = """
                [
                {
                    "fileName": "1111.png",
                    "imgUrl": "1111.url"
                },
                {
                    "fileName": "2222.png",
                    "imgUrl": "2222.url"
                },
                {
                    "fileName": "3333.png",
                    "imgUrl": "3333.url"
                },
                {
                    "fileName": "4444.png",
                    "imgUrl": "4444.url"
                },
                {
                    "fileName": "5555.png",
                    "imgUrl": "5555.url"
                }
            ]""")
    private List<FileDto> postImgs;
}
