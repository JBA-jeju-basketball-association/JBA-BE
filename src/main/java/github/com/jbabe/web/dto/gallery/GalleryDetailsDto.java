package github.com.jbabe.web.dto.gallery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
            "        \"fileName\": \"3.png\",\n" +
            "        \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"36.png\",\n" +
            "        \"imgUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/930d3711-de9d-4f31-9e73-59a3242477c7.png\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"35.png\",\n" +
            "        \"imgUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"36.png\",\n" +
            "        \"imgUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/930d3711-de9d-4f31-9e73-59a3242477c7.png\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"35.png\",\n" +
            "        \"imgUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png\"\n" +
            "    }\n" +
            "]"
    )
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
