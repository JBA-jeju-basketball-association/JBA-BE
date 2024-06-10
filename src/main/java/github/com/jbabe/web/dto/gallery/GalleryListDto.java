package github.com.jbabe.web.dto.gallery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GalleryListDto {
    private Integer galleryId;
    private String title;
    private String fileName;
    private String imgUrl;
    private String createAt;
}
