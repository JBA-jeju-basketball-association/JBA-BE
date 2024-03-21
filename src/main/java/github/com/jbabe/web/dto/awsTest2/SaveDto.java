package github.com.jbabe.web.dto.awsTest2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveDto {
    private List<MultipartFile> uploadFiles = new ArrayList<>();
    private SaveFileType type;
}
