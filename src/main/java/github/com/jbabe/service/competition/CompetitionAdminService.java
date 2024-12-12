package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFile;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFileJpa;
import github.com.jbabe.repository.competitionImg.CompetitionImg;
import github.com.jbabe.repository.competitionImg.CompetitionImgJpa;
import github.com.jbabe.repository.competitionPlace.CompetitionPlace;
import github.com.jbabe.repository.competitionPlace.CompetitionPlaceJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.repository.division.DivisionEnumJpa;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.CompetitionMapper;
import github.com.jbabe.service.storage.ServerDiskService;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.competition.CompetitionAdminListRequest;
import github.com.jbabe.web.dto.competition.CompetitionDetailAttachedFile;
import github.com.jbabe.web.dto.competition.GetCompetitionAdminListResponse;
import github.com.jbabe.web.dto.competition.GetTotalCompetitionAndDivisionList;
import github.com.jbabe.web.dto.competition.tempDto.ListAndTotalElements;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionAdminService {

    private final CompetitionJpa competitionJpa;
    private final DivisionJpa divisionJpa;
    private final CompetitionAttachedFileJpa competitionAttachedFileJpa;
    private final DivisionEnumJpa divisionEnumJpa;
    private final CompetitionRecordJpa competitionRecordJpa;
    private final CompetitionImgJpa competitionImgJpa;
    private final CompetitionPlaceJpa competitionPlaceJpa;
    private final ServerDiskService serverDiskService;


    public Page<GetCompetitionAdminListResponse> getCompetitionAdminList(String searchType, String searchKey, LocalDate filterStartDate, LocalDate filterEndDate, String division, String situation, Pageable pageable) {
        LocalDateTime filterStartTime;
        LocalDateTime filterEndTime;
        String realSearchKey = searchKey;
        Integer numberSearchKey = 0;
        try {
            numberSearchKey = Integer.parseInt(realSearchKey);
        }catch (NumberFormatException e) {}

        if (filterStartDate != null) {
            filterStartTime = filterStartDate.atTime(LocalTime.of(0,0,0));
        }else filterStartTime = null;

        if (filterEndDate != null) {
            filterEndTime = filterEndDate.atTime(LocalTime.of(0,0,0));
        }else filterEndTime = null;

        if (Objects.equals(searchKey, "")) realSearchKey = null;

        Page<GetCompetitionAdminListResponse> responses =
                competitionJpa.competitionAdminList(searchType, realSearchKey, numberSearchKey, filterStartTime, filterEndTime, division, situation, pageable);

        responses.getContent().forEach(item -> {
            if (item.getContent() != null) item.setContent(Jsoup.parse(item.getContent()).text().replace("\u00A0", " "));
            item.setDivisions(divisionJpa.findAllByCompetitionCompetitionId(item.getCompetitionId()).stream().map(Division::getDivisionName).toList());
            item.setFiles(competitionAttachedFileJpa.findAllByCompetitionCompetitionId(item.getCompetitionId()).stream().map(a ->
                            CompetitionDetailAttachedFile.builder()
                                    .competitionAttachedFileId(a.getCompetitionAttachedFileId())
                                    .filePath(a.getFilePath())
                                    .fileName(a.getFileName())
                                    .build()
                    ).toList()
            );
        });

        return responses;
    }

    public GetTotalCompetitionAndDivisionList getTotalCompetitionAndDivisionList() {

        return GetTotalCompetitionAndDivisionList.builder()
                .totalSize(competitionJpa.findAll().size())
                .divisionList(divisionEnumJpa.findAll().stream().map(DivisionEnum::getDivisionName).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public String deleteCompetition(Integer id) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("해당 id로 대회를 찾을 수 없습니다.", id));
        List<CompetitionAttachedFile> competitionAttachedFiles = competitionAttachedFileJpa.findAllByCompetition(competition);
        List<CompetitionPlace> competitionPlaces = competitionPlaceJpa.findAllByCompetition(competition);
        List<CompetitionImg> competitionImgs = competitionImgJpa.findAllByCompetition(competition);
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);


        List<String> deleteFiles = new ArrayList<>();
        competitionAttachedFiles.forEach(item -> {
            if (item.getFilePath() != null) deleteFiles.add(item.getFilePath());
        });
        competitionImgs.forEach(item -> {
            if (item.getImgUrl() != null) deleteFiles.add(item.getImgUrl());
        });
        divisions.forEach(division -> {
                    List<CompetitionRecord> competitionRecords = competitionRecordJpa.findAllByDivision(division);
                    competitionRecords.forEach(item -> {
                        if (item.getFilePath() != null && !item.getFilePath().isEmpty()) deleteFiles.add(item.getFilePath());
                    });
                    competitionRecordJpa.deleteAll(competitionRecords);
                }

        );
        if (!deleteFiles.isEmpty()) {
            serverDiskService.fileDelete(deleteFiles);
        }
        divisionJpa.deleteAll(divisions);
        competitionImgJpa.deleteAll(competitionImgs);
        competitionPlaceJpa.deleteAll(competitionPlaces);
        competitionAttachedFileJpa.deleteAll(competitionAttachedFiles);
        competitionJpa.delete(competition);

        return "OK";
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


    public Page<GetCompetitionAdminListResponse> getAListOfCompetitionsForAdmin(CompetitionAdminListRequest request) {
        Page<GetCompetitionAdminListResponse>  queryResult = competitionJpa.findAListOfCompetitionsForAdmin(request);
        List<Integer> ids = queryResult.stream().map(GetCompetitionAdminListResponse::getCompetitionId).toList();
        Map<Integer, List<CompetitionDetailAttachedFile>> files = competitionJpa.getCompetitionFiles(ids);
        Map<Integer, List<String>> divisionNames = competitionJpa.getDivisionNames(ids);

        queryResult.forEach(item ->{
            item.setFiles(files.get(item.getCompetitionId()));
            item.setDivisions(divisionNames.get(item.getCompetitionId()));
        });

        return queryResult;
    }
}
