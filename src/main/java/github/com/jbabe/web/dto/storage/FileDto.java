package github.com.jbabe.web.dto.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileDto {
    private String fileName;
    private String fileUrl;
}
