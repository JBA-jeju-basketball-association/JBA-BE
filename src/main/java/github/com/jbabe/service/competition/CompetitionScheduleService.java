package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.competition.GetScheduleResponse;
import github.com.jbabe.web.dto.competition.GetScheduleRow;
import github.com.jbabe.web.dto.competition.PostCompetitionScheduleRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (!competition.getPhase().equals(Competition.Phase.INFO)) throw new BadRequestException("이미 일정이 등록된 대회입니다.", id);

        competitionJpa.save(Competition.builder()
                .competitionId(id)
                .user(competition.getUser())
                .competitionName(competition.getCompetitionName())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .relatedUrl(competition.getRelatedUrl())
                .content(competition.getContent())
                .phase(Competition.Phase.SCHEDULE)
                .competitionStatus(competition.getCompetitionStatus())
                .createAt(competition.getCreateAt())
                .updateAt(competition.getUpdateAt())
                .deleteAt(competition.getDeleteAt())
                .build());

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
                    .state5x5(row.isState5x5())
                    .build()).toList();
            competitionRecordJpa.saveAll(competitionRecords);
        });

        return "OK";
    }

    public List<GetScheduleResponse> getCompetitionSchedule(Integer id) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);
        List<GetScheduleResponse> getScheduleResponses = divisions.stream().map(division -> (
                GetScheduleResponse.builder()
                        .division(division.getDivisionName())
                        .getScheduleRows(
                                competitionRecordJpa.findAllByDivision(division).stream().map(record ->
                                        GetScheduleRow.builder()
                                                .competitionResultId(record.getCompetitionRecordId())
                                                .gameNumber(record.getGameNumber())
                                                .startDate(record.getTime())
                                                .floor(record.getFloor())
                                                .place(record.getPlace())
                                                .homeName(record.getHomeName())
                                                .awayName(record.getAwayName())
                                                .state5x5(record.isState5x5())
                                                .build()
                                ).collect(Collectors.toList())
                        )
                        .build()
        )).toList();
        if (getScheduleResponses.isEmpty()) throw new NotFoundException("대회 일정을 찾을 수 없습니다.", id);

        return getScheduleResponses;

    }

    @Transactional
    public String deleteCompetitionSchedule(Integer id) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);
        Competition fixPhaseCompetition = Competition.builder()
                .competitionId(competition.getCompetitionId())
                .user(competition.getUser())
                .competitionName(competition.getCompetitionName())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .relatedUrl(competition.getRelatedUrl())
                .content(competition.getContent())
                .phase(Competition.Phase.INFO)
                .competitionStatus(competition.getCompetitionStatus())
                .createAt(competition.getCreateAt())
                .updateAt(competition.getUpdateAt())
                .deleteAt(competition.getDeleteAt())
                .build();
        competitionJpa.save(fixPhaseCompetition);

        divisions.forEach((division ->
                    competitionRecordJpa.deleteAll(competitionRecordJpa.findAllByDivision(division))
                ));
        return "OK";
    }

    @Transactional
    public String updateCompetitionSchedule(Integer id, List<PostCompetitionScheduleRequest> requests) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);
        if (!competition.getPhase().equals(Competition.Phase.SCHEDULE)) throw new BadRequestException("일정등록 단계가 아닙니다.", id);

        divisions.forEach((division ->
                competitionRecordJpa.deleteAll(competitionRecordJpa.findAllByDivision(division))
        ));

        requests.forEach((r) ->{
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
                            .state5x5(row.isState5x5())
                            .build()).toList();
            competitionRecordJpa.saveAll(competitionRecords);
        });
        return "OK";
    }
}
