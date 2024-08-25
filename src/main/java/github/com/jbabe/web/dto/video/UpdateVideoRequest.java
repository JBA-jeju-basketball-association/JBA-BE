package github.com.jbabe.web.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateVideoRequest {
    @NotNull
    private Integer videoId;

    @NotBlank
    @NotNull
    private String title;

    @NotBlank
    @NotNull
    private String url;

    private String content;

}
