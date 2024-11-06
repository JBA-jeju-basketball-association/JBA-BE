package github.com.jbabe.web.dto.competition.participate;

import github.com.jbabe.web.dto.storage.FileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ParticipateResponse {
    private String competitionName;
    private String divisionName;
    private String applicantName;
    private String applicantPhoneNum;
    private String applicantEmail;
    private LocalDateTime applicantDate;
    private LocalDate competitionStartDate;
    private LocalDate competitionEndDate;
    private LocalDate participationStartDate;
    private LocalDate participationEndDate;
    private List<FileDto> fileDtos;

}
