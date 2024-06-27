package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.competition.PostCompetitionScheduleRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionScheduleService {

    private final CompetitionJpa competitionJpa;
    private final DivisionJpa divisionJpa;
    private final CompetitionRecordJpa competitionRecordJpa;

    @Transactional
    public String postCompetitionSchedule(List<PostCompetitionScheduleRequest> request, Integer id) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);

        request.forEach((r) ->{
            Division division = divisions.stream().filter((d) -> Objects.equals(r.getDivision(), d.getDivisionName())).toList().get(0);
            List<CompetitionRecord> competitionRecords =  r.getPostCompetitionScheduleRow().stream().map((row) ->
                    CompetitionRecord.builder()
                    .division(division)
                    .floor(row.getFloor())
                    .place(row.getPlace())
                    .gameNumber(row.getGameNumber())
                    .time(row.getStartDate())
                    .homeName(row.getHomeName())
                    .awayName(row.getAwayName())
                    .is5x5(row.isState5x5())
                    .build()).toList();
            System.out.println(r.getPostCompetitionScheduleRow().get(0).isState5x5());
            competitionRecordJpa.saveAll(competitionRecords);
        });

        return "OK";
    }
}
