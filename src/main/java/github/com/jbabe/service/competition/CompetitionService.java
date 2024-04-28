package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFile;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFileJpa;
import github.com.jbabe.repository.competitionImg.CompetitionImg;
import github.com.jbabe.repository.competitionImg.CompetitionImgJpa;
import github.com.jbabe.repository.competitionPlace.CompetitionPlace;
import github.com.jbabe.repository.competitionPlace.CompetitionPlaceJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.AddCompetitionRequest;
import github.com.jbabe.web.dto.competition.CompetitionListResponse;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionJpa competitionJpa;
    private final UserJpa userJpa;
    private final CompetitionImgJpa competitionImgJpa;
    private final StorageService storageService;
    private final CompetitionAttachedFileJpa competitionAttachedFileJpa;
    private final CompetitionPlaceJpa competitionPlaceJpa;
    private final DivisionJpa divisionJpa;

    @Transactional
    public String addCompetitionInfo(AddCompetitionRequest addCompetitionRequest, List<MultipartFile> files, Optional<SaveFileType> type, CustomUserDetails customUserDetails) {
        User user = userJpa.findById(customUserDetails.getUserId())
                .orElseThrow(()-> new NotFoundException("user을 찾을 수 없습니다.", customUserDetails.getUserId()));

        // competition table save
        Competition competition = Competition.builder()
                .user(user)
                .competitionName(addCompetitionRequest.getTitle())
                .startDate(addCompetitionRequest.getStartDate())
                .endDate(addCompetitionRequest.getEndDate())
                .relatedUrl(addCompetitionRequest.getRelatedURL())
                .content(addCompetitionRequest.getSkData())
                .competitionStatus(Competition.CompetitionStatus.NORMAL)
                .createAt(LocalDateTime.now())
                .build();
        competitionJpa.save(competition);

        // competition_img table save
        List<CompetitionImg> competitionImgs = addCompetitionRequest.getRealCkImgs().stream()
                .map((img) -> CompetitionImg.builder()
                        .competition(competition)
                        .imgUrl(img)
                        .fileName(img)
                        .build()).toList();
        competitionImgJpa.saveAll(competitionImgs);


        // competition_attached_file table save
        if (files != null) {
            List<FileDto> response = storageService.fileUploadAndGetUrl(files, type.orElseGet(()-> SaveFileType.small));
            List<CompetitionAttachedFile> competitionAttachedFiles = response.stream()
                    .map((res) -> CompetitionAttachedFile.builder()
                            .competition(competition)
                            .filePath(res.getFileUrl())
                            .fileName(res.getFileName())
                            .build()).toList();
            competitionAttachedFileJpa.saveAll(competitionAttachedFiles);
        }


        // competition_place table save
        List<CompetitionPlace> competitionPlaces = addCompetitionRequest.getPlaces().stream()
                .map((place) -> CompetitionPlace.builder()
                        .competition(competition)
                        .placeName(place.getName())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .address(place.getAddress())
                        .build()).toList();
        competitionPlaceJpa.saveAll(competitionPlaces);

        //division table save
        List<Division> divisions = addCompetitionRequest.getDivisions().stream()
                .map((division) -> Division.builder()
                        .competition(competition)
                        .divisionName(division)
                        .build()).toList();
        divisionJpa.saveAll(divisions);

        return competition.getCompetitionName();
    }


    public Page<CompetitionListResponse> getCompetitionList(String status, String year, Pageable pageable) {

        Date startDateFilter = Competition.getStartTimeThisYear(year);
        Date endDateFilter = Competition.getEndTimeThisYear(year);

        return competitionJpa.findAllCompetitionPagination(status, startDateFilter, endDateFilter, pageable);
    }

    public List<Integer> getCompetitionYearList() {
        List<Competition> competitions = competitionJpa.findAll();
        List<Integer> yearList = new ArrayList<>();;
        competitions.forEach((c) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(c.getStartDate());
                    int year = calendar.get(Calendar.YEAR);
                    if (!yearList.contains(year)) yearList.add(year);
                }
        );
        return yearList;
    }
}
