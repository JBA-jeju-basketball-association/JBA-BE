package github.com.jbabe.web.dto.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ManagePostsDto {
    private int postId;
    private String email;
    private String category;
    private String thumbnail;
    @Schema(description = "제목", example = "게시물 입니다.")
    private String title;
    private String foreword;
    @Schema(description = "내용", example = "<p>POST 페이지 더미데이터입니다.</p>")
    private String content;

    @Schema(description = "첨부 파일 정보", example = """
            [
                {
                    "fileName": "파일1.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일2.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일3.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일4.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일5.png",
                    "fileUrl": "첨부파일 url~~~~"
                }
            ]""")
    private List<FileDto> files;
    private String postStatus;
    private Boolean isAnnouncement;
    private String createAt;
    private String updateAt;
    private String deleteAt;
}
