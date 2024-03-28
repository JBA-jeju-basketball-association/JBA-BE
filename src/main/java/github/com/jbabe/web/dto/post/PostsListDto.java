package github.com.jbabe.web.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostsListDto {
    private long postId;
    private Boolean isAnnouncement;
    private String title;
    private String writer;
    private String createAt;
    private long viewCount;
}
