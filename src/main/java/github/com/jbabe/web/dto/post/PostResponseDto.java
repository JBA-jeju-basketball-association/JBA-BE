package github.com.jbabe.web.dto.post;

import github.com.jbabe.web.dto.storage.FileDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private long postId;
    private String foreword;
    private Boolean isAnnouncement;
    private String title;
    private String writer;
    private String createAt;
    private long viewCount;
    private List<FileDto> files;
    private List<FileDto> postImgs;
    private String content;
}
