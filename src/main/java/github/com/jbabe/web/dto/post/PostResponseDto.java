package github.com.jbabe.web.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private long postId;
    private String title;
    private String writer;
    private String createAt;
    private long viewCount;
    private List<String> postAttachedFiles;
    private List<String> postImgs;
    private String content;
}
