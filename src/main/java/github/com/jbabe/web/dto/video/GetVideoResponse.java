package github.com.jbabe.web.dto.video;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetVideoResponse {
    private Integer videoId;

    private String author;

    private String title;

    private String url;

    private String content;

    private LocalDateTime createAt;


}
