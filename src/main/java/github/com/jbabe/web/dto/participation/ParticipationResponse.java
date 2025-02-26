package github.com.jbabe.web.dto.participation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ParticipationResponse {
    private Integer divisionId;
    private String divisionName;
    @Setter
    private List<ParticipationDto> participationList;

    @Getter
    public static class ParticipationDto {
        private Long participationId;
        private String name;
        private String phoneNum;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        @JsonIgnore
        private File file;
        @Setter
        private List<File> files;

        private ApplicantInfo applicantInfo;

        @Getter
        public static class ApplicantInfo{
            private Integer userId;
            private String name;
        }

        @Getter
        public static class File {
            private String fileName;
            private String filePath;
        }
    }


}
