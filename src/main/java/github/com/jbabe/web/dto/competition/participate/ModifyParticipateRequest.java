package github.com.jbabe.web.dto.competition.participate;

import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ModifyParticipateRequest extends ParticipateRequest{
    @Schema(description = "신청서 수정 시 남길 파일들의 id 리스트",
            example = """
            [
                {
                    "fileName": "파일1.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일2.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일3.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일4.png",
                    "fileUrl": "첨부파일 url~~~~"
                },
                {
                    "fileName": "파일5.png",
                    "fileUrl": "첨부파일 url~~~~"
                }
            ]""")
    private List<FileDto> remainingFiles;
}
