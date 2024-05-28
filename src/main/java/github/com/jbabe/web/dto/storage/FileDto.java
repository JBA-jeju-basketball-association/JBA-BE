package github.com.jbabe.web.dto.storage;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileDto {
    private String fileName;
    @JsonAlias({"fileUrl", "imgUrl"})
    private String fileUrl;
}
