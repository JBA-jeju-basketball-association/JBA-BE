package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.competition.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionResultService {

    private final CompetitionJpa competitionJpa;
    private final DivisionJpa divisionJpa;
    private final CompetitionRecordJpa competitionRecordJpa;


    @Transactional
    public String postCompetitionResult(Integer id, List<PostResultRequest> requests) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);
        if (competition.getPhase().equals(Competition.Phase.INFO)) throw new BadRequestException("일정 먼저 입력 바랍니다.", id);

        competitionJpa.save(Competition.builder()
                .competitionId(id)
                .user(competition.getUser())
                .competitionName(competition.getCompetitionName())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .relatedUrl(competition.getRelatedUrl())
                .content(competition.getContent())
                .phase(Competition.Phase.FINISH)
                .competitionStatus(competition.getCompetitionStatus())
                .createAt(competition.getCreateAt())
                .updateAt(competition.getUpdateAt())
                .deleteAt(competition.getDeleteAt())
                .build());
//        List<Integer> competitionRecordIds = requests.stream().map(item -> {
//
//        })
//        divisions.forEach((division ->
//                competitionRecordJpa.deleteAll(competitionRecordJpa.findAllByDivision(division))
//        {
//            List<CompetitionRecord> competitionRecords = competitionRecordJpa.findAllByDivision(division);
//            competitionRecords.forEach(item -> {
//                item.getCompetitionRecordId()
//            });
//        }
//        ));



        requests.forEach((request) ->
                    request.getPostResultRequestRows().forEach((row) -> {
                        Division division = divisions.stream().filter((item) -> Objects.equals(item.getDivisionName(), request.getDivision())).toList().get(0);
                        if (row.getCompetitionResultId() != null) {
                            CompetitionRecord record = competitionRecordJpa.findById(row.getCompetitionResultId()).orElseThrow(() -> new NotFoundException("대회기록을 찾을 수 없습니다.", row.getCompetitionResultId()));
                            System.out.println(record.getTime());
                            System.out.println(row.getStartDate());
                            competitionRecordJpa.save(CompetitionRecord.builder()
                                    .competitionRecordId(row.getCompetitionResultId())
                                    .division(division)
                                    .floor(row.getFloor())
                                    .place(row.getPlace())
                                    .gameNumber(row.getGameNumber())
                                    .time(row.getStartDate().plusHours(9))
                                    .homeName(row.getHomeName())
                                    .homeScore(row.getHomeScore())
                                    .awayName(row.getAwayName())
                                    .awayScore(row.getAwayScore())
                                    .filePath(row.getFilePath())
                                    .fileName(row.getFileName())
                                    .state5x5(row.isState5x5())
                                    .build());


                        } else {
                            competitionRecordJpa.save(CompetitionRecord.builder()
                                    .division(division)
                                    .floor(row.getFloor())
                                    .place(row.getPlace())
                                    .gameNumber(row.getGameNumber())
                                    .time(row.getStartDate().plusHours(9))
                                    .homeName(row.getHomeName())
                                    .homeScore(row.getHomeScore())
                                    .awayName(row.getAwayName())
                                    .awayScore(row.getAwayScore())
                                    .filePath(row.getFilePath())
                                    .fileName(row.getFileName())
                                    .state5x5(row.isState5x5())
                                    .build());
                        }
                    })
        );

        return "OK";
    }

    public List<GetResultResponse> getCompetitionResult(Integer id) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);

        List<GetResultResponse> getResultResponses = divisions.stream().map(division -> (
                GetResultResponse.builder()
                        .division(division.getDivisionName())
                        .getResultResponseRows(
                                competitionRecordJpa.findAllByDivision(division).stream().map(record ->
                                        GetResultResponseRow.builder()
                                                .competitionResultId(record.getCompetitionRecordId())
                                                .gameNumber(record.getGameNumber())
                                                .startDate(record.getTime())
                                                .floor(record.getFloor())
                                                .place(record.getPlace())
                                                .homeName(record.getHomeName())
                                                .homeScore(record.getHomeScore())
                                                .awayName(record.getAwayName())
                                                .awayScore(record.getAwayScore())
                                                .filePath(record.getFilePath())
                                                .fileName(record.getFileName())
                                                .state5x5(record.isState5x5())
                                                .build()
                                ).collect(Collectors.toList())
                        )
                        .build()
        )).toList();

        return getResultResponses;
    }
}
