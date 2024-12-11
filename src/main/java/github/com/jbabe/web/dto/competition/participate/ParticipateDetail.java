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
public class ParticipateDetail extends SimplyParticipateResponse{
    private String email;
    private String name;
    private String phoneNum;
    private Integer competitionId;
    private LocalDate competitionStartDate;
    private LocalDate competitionEndDate;
    private List<FileDto> files;
}
