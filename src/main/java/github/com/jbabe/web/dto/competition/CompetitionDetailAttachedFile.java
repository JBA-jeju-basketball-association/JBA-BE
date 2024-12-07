package github.com.jbabe.web.dto.competition;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "competitionAttachedFileId")
public class CompetitionDetailAttachedFile {
    private Integer competitionAttachedFileId;
    private String filePath;
    private String fileName;
}
