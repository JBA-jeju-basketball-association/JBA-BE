package github.com.jbabe.web.dto.gallery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GalleryDetailsDto {
    private String title;
    @JsonAlias({"data", "imgs"})
    private List<FileDto> files;


//    @NoArgsConstructor
//    @Getter
//    @Setter
//    public static class Img{
//        private String fileName;
//        @JsonAlias({"fileUrl", "imgUrl"})
//        private String imgUrl;
//    }

}
