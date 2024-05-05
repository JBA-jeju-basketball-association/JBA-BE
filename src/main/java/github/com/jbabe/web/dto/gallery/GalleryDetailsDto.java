package github.com.jbabe.web.dto.gallery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GalleryDetailsDto {
    private String title;
    private List<Img> imgs;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Img{
        private String fileName;
        private String imgUrl;
    }

}
