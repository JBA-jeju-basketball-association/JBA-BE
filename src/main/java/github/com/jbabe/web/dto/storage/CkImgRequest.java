package github.com.jbabe.web.dto.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CkImgRequest {
    @Schema(description = "이미지 url", example = "https://~~~~")
    private String fileUrl;
    @Schema(description = "이미지 이름", example = "도민체전 참가신청서")
    private String fileName;
}
