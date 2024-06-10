package github.com.jbabe.web.dto.post;

import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class PostReqDto {
    @Schema(description = "제목", example = "게시물 입니다.")
    private String title;
    @Schema(description = "요청 메세지", example = "<p>POST 페이지 더미데이터입니다.</p>")
    private String content;
    @Schema(description = "머릿말", example = "합격자 발표")
    private String foreword;

    private List<FileDto> postImgs;
}
