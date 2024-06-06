package github.com.jbabe.web.dto.gallery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GalleryDetailsDto {
    @Schema(description = "제목", example = "갤러리 게시물 입니다.")
    private String title;
    @JsonAlias({"data", "imgs"})
    @Schema(name = "imgs",description = "첨부파일들", example = "[\n" +
            "    {\n" +
            "        \"fileName\": \"1111.png\",\n" +
            "        \"imgUrl\": \"1111.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"2222.png\",\n" +
            "        \"imgUrl\": \"2222.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"3333.png\",\n" +
            "        \"imgUrl\": \"3333.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"4444.png\",\n" +
            "        \"imgUrl\": \"4444.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"5555.png\",\n" +
            "        \"imgUrl\": \"5555.url\"\n" +
            "    }\n" +
            "]"
    )
    @NotNull(message = "적어도 한개의 imgs는 필수입니다.")
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
