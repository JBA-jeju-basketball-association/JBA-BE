package github.com.jbabe.web.dto.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PostReqDto {
    private String title;
    private String content;
    private List<FileDto> files;
}
