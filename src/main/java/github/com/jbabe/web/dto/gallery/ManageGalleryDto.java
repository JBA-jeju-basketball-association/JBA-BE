package github.com.jbabe.web.dto.gallery;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ManageGalleryDto {
    @Schema(description = "갤러리 ID", example = "1")
    private int galleryId;
    @Schema(description = "이메일", example = "abc@abc.com")
    private String email;
    @Schema(description = "접근권한", example = "true : 스탭전용, false : 전체공개")
    private Boolean isOfficial;
    @Schema(description = "�����", example = "http://asdffsd.cac/asdfkcnas.png")
    private String thumbnail;
    @Schema(description = "제목", example = "갤러리입니다.ㅏ")
    private String title;

    @Schema(description = "첨부이미지", example = """
            [
                {
                    "fileName": "file1.png",
                    "fileUrl": "abc.url~~~~"
                },
                {
                    "fileName": "file2.png",
                    "fileUrl": "abc.url~~~~"
                },
                {
                    "fileName": "file3.png",
                    "fileUrl": "abc.url~~~~"
                },
                {
                    "fileName": "file4.png",
                    "fileUrl": "abc.url~~~~"
                },
                {
                    "fileName": "file5.png",
                    "fileUrl": "url~~~~"
                }
            ]""")
    private List<FileDto> files;
    @Schema(description = "갤러리 상태", example = "normal : 공개, hide : 숨김, delete : 삭제")
    private String galleryStatus;
    @Schema(description = "작성날짜", example = "2021-09-01 00:00:00")
    private String createAt;
    @Schema(description = "수정날짜", example = "2021-09-01 00:00:00")
    private String updateAt;
    @Schema(description = "삭제날짜", example = "2021-09-01 00:00:00")
    private String deleteAt;
}
