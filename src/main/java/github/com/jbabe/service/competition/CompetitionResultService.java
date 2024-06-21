package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.competition.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionResultService {

    private final CompetitionJpa competitionJpa;
    private final DivisionJpa divisionJpa;
    private final CompetitionRecordJpa competitionRecordJpa;


    @Transactional
    public String addCompetitionResult(Integer id, List<AddCompetitionResultRequest> request) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("해당 id로 대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);

        List<CompetitionRecord> competitionRecords = new ArrayList<>();
        request.forEach((req) ->
                req.getCompetitionResult().forEach((result) -> {
                    CompetitionRecord data = CompetitionRecord.builder()
                            .division(divisions.stream().filter((d) -> d.getDivisionName().equals(result.getDivision())).toList().get(0))
                            .floor(req.getFloor())
                            .time(result.getStartTime())
                            .homeName(result.getHomeName())
                            .homeScore(result.getHomeScore())
                            .awayName(result.getAwayName())
                            .awayScore(result.getAwayScore())
                            .filePath(result.getFileUrl() == null || result.getFileUrl().isEmpty() ? null : result.getFileUrl())
                            .fileName(result.getFileName() == null || result.getFileName().isEmpty() ? null : result.getFileName())
                            .build();
                    competitionRecords.add(data);
                })
        );
        competitionRecordJpa.saveAll(competitionRecords);
        return "OK";
    }

    public GetResultResponse getCompetitionResult(Integer id) {
        List<AddCompetitionResultRequest> data = new ArrayList<>();
        List<String> divisions = new ArrayList<>();
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("해당 id로 대회를 찾을 수 없습니다.", id));
        List<Division> divisionList = divisionJpa.findAllByCompetition(competition);
        List<String> floorList = new ArrayList<>();
        List<ResultListWithFloor> list = new ArrayList<>();
        divisionList.forEach((d) -> {
            divisions.add(d.getDivisionName());
            List<CompetitionRecord> competitionRecords = competitionRecordJpa.findAllByDivision(d);
            competitionRecords.forEach((c) -> {
                if (!floorList.contains(c.getFloor())) {
                    floorList.add(c.getFloor());
                }
                list.add(ResultListWithFloor.builder()
                        .competitionRecordId(c.getCompetitionRecordId())
                        .floor(c.getFloor())
                        .division(d.getDivisionName())
                        .time(c.getTime())
                        .homeName(c.getHomeName())
                        .homeScore(c.getHomeScore())
                        .awayName(c.getAwayName())
                        .awayScore(c.getAwayScore())
                        .filePath(c.getFilePath())
                        .fileName(c.getFileName())
                        .build());
            });
        });

        floorList.forEach((floor) -> {
            List<CompetitionResult> results = new ArrayList<>();
            list.stream().filter((l) -> floor.equals(l.getFloor())).forEach((fl) ->
                    results.add(CompetitionResult.builder()
                            .competitionRecordId(fl.getCompetitionRecordId())
                            .division(fl.getDivision())
                            .startTime(fl.getTime())
                            .homeName(fl.getHomeName())
                            .homeScore(fl.getHomeScore())
                            .awayName(fl.getAwayName())
                            .awayScore(fl.getAwayScore())
                            .fileUrl(fl.getFilePath())
                            .fileName(fl.getFileName())
                            .build())
            );
            data.add(AddCompetitionResultRequest.builder()
                    .floor(floor)
                    .competitionResult(results)
                    .build());
        });

        return new GetResultResponse(divisions, data);
    }

    public GetResultWithTitleResponse getCompetitionResultWithTitle(Integer id) {
        List<AddCompetitionResultRequest> data = new ArrayList<>();
        List<String> divisions = new ArrayList<>();
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("해당 id로 대회를 찾을 수 없습니다.", id));
        List<Division> divisionList = divisionJpa.findAllByCompetition(competition);
        List<String> floorList = new ArrayList<>();
        List<ResultListWithFloor> list = new ArrayList<>();
        divisionList.forEach((d) -> {
            divisions.add(d.getDivisionName());
            List<CompetitionRecord> competitionRecords = competitionRecordJpa.findAllByDivision(d);
            competitionRecords.forEach((c) -> {
                if (!floorList.contains(c.getFloor())) {
                    floorList.add(c.getFloor());
                }
                list.add(ResultListWithFloor.builder()
                        .competitionRecordId(c.getCompetitionRecordId())
                        .floor(c.getFloor())
                        .division(d.getDivisionName())
                        .time(c.getTime())
                        .homeName(c.getHomeName())
                        .homeScore(c.getHomeScore())
                        .awayName(c.getAwayName())
                        .awayScore(c.getAwayScore())
                        .filePath(c.getFilePath())
                        .fileName(c.getFileName())
                        .build());
            });
        });

        floorList.forEach((floor) -> {
            List<CompetitionResult> results = new ArrayList<>();
            list.stream().filter((l) -> floor.equals(l.getFloor())).forEach((fl) ->
                    results.add(CompetitionResult.builder()
                            .competitionRecordId(fl.getCompetitionRecordId())
                            .division(fl.getDivision())
                            .startTime(fl.getTime())
                            .homeName(fl.getHomeName())
                            .homeScore(fl.getHomeScore())
                            .awayName(fl.getAwayName())
                            .awayScore(fl.getAwayScore())
                            .fileUrl(fl.getFilePath())
                            .fileName(fl.getFileName())
                            .build())
            );
            data.add(AddCompetitionResultRequest.builder()
                    .floor(floor)
                    .competitionResult(results)
                    .build());
        });

        return new GetResultWithTitleResponse(competition.getCompetitionName(), competition.getStartDate(), competition.getEndDate(), divisions, data);
    }

    public String updateCompetitionResult(Integer id, List<AddCompetitionResultRequest> request) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("해당 id로 대회를 찾을 수 없습니다.", id));
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);



        List<CompetitionRecord> competitionRecords = new ArrayList<>();
        List<CompetitionRecord> newCompetitionRecords = new ArrayList<>();
        request.forEach((req) ->
                req.getCompetitionResult().forEach((result) -> {
                    if (result.getCompetitionRecordId() == null) {
                        CompetitionRecord data = CompetitionRecord.builder()
                                .division(divisions.stream().filter((d) -> d.getDivisionName().equals(result.getDivision())).toList().get(0))
                                .floor(req.getFloor())
                                .time(result.getStartTime())
                                .homeName(result.getHomeName())
                                .homeScore(result.getHomeScore())
                                .awayName(result.getAwayName())
                                .awayScore(result.getAwayScore())
                                .filePath(result.getFileUrl() == null || result.getFileUrl().isEmpty() ? null : result.getFileUrl())
                                .fileName(result.getFileName() == null || result.getFileName().isEmpty() ? null : result.getFileName())
                                .build();
                        competitionRecords.add(data);
                    }else {
                        CompetitionRecord data = CompetitionRecord.builder()
                                .competitionRecordId(result.getCompetitionRecordId())
                                .division(divisions.stream().filter((d) -> d.getDivisionName().equals(result.getDivision())).toList().get(0))
                                .floor(req.getFloor())
                                .time(result.getStartTime())
                                .homeName(result.getHomeName())
                                .homeScore(result.getHomeScore())
                                .awayName(result.getAwayName())
                                .awayScore(result.getAwayScore())
                                .filePath(result.getFileUrl() == null || result.getFileUrl().isEmpty() ? null : result.getFileUrl())
                                .fileName(result.getFileName() == null || result.getFileName().isEmpty() ? null : result.getFileName())
                                .build();
                        newCompetitionRecords.add(data);
                    }

                })
        );
        competitionRecordJpa.saveAll(newCompetitionRecords);
        competitionRecordJpa.saveAll(competitionRecords);
        return "OK";
    }
}
