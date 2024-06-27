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
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.*;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionDetailService {

    private final UserJpa userJpa;
    private final CompetitionJpa competitionJpa;
    private final CompetitionImgJpa competitionImgJpa;
    private final StorageService storageService;
    private final CompetitionPlaceJpa competitionPlaceJpa;
    private final DivisionJpa divisionJpa;
    private final CompetitionAttachedFileJpa competitionAttachedFileJpa;
    private final CompetitionRecordJpa competitionRecordJpa;


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
                .content(addCompetitionRequest.getCkData())
                .phase(Competition.Phase.INFO)
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
                        .placeName(place.getPlaceName())
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


    public CompetitionDetailResponse getCompetitionDetail(Integer id) {
        Competition competition = competitionJpa.findById(id)
                .orElseThrow(()-> new NotFoundException("해당 아이디와 일치하는 대회를 찾을 수 없습니다.", id));
        if (!competition.getCompetitionStatus().equals(Competition.CompetitionStatus.NORMAL)) throw new NotFoundException("대회 조회가 불가능합니다.", competition.getCompetitionStatus());
        List<CompetitionPlace> competitionPlaces = competitionPlaceJpa.findAllByCompetition(competition);
        List<CompetitionAttachedFile> competitionAttachedFiles = competitionAttachedFileJpa.findAllByCompetition(competition);
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);

        List<CompetitionDetailPlace> competitionDetailPlaces = competitionPlaces.stream().map((p)->
                CompetitionDetailPlace.builder()
                        .competitionPlaceId(p.getCompetitionPlaceId())
                        .placeName(p.getPlaceName())
                        .latitude(p.getLatitude())
                        .longitude(p.getLongitude())
                        .address(p.getAddress())
                        .build()
        ).toList();

        List<CompetitionDetailAttachedFile> competitionDetailAttachedFiles = competitionAttachedFiles.stream().map((f)->
                CompetitionDetailAttachedFile.builder()
                        .competitionAttachedFileId(f.getCompetitionAttachedFileId())
                        .filePath(f.getFilePath())
                        .fileName(f.getFileName())
                        .build()
        ).toList();

        List<String> divisionList = new ArrayList<>();
        divisions.forEach((d)-> divisionList.add(d.getDivisionName()));

        List<CompetitionImg> competitionImgs = competitionImgJpa.findAllByCompetition(competition);


        return CompetitionDetailResponse.builder()
                .competitionId(competition.getCompetitionId())
                .title(competition.getCompetitionName())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .relatedUrl(competition.getRelatedUrl())
                .content(competition.getContent())
                .phase(competition.getPhase())
                .places(competitionDetailPlaces)
                .competitionDetailAttachedFiles(competitionDetailAttachedFiles)
                .divisions(divisionList)
                .ckImgUrls(competitionImgs.stream().map(CompetitionImg::getImgUrl).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public Integer updateCompetition(Integer id, UpdateCompetitionRequest request, List<MultipartFile> files, Optional<SaveFileType> type) {
        Competition competition = competitionJpa.findById(id).orElseThrow(() -> new NotFoundException("id에 해당하는 대회를 찾을 수 없습니다.", id));
        List<CompetitionImg> competitionImgs = competitionImgJpa.findAllByCompetition(competition);
        List<CompetitionAttachedFile> competitionAttachedFiles = competitionAttachedFileJpa.findAllByCompetition(competition);
        List<Division> divisions = divisionJpa.findAllByCompetition(competition);

        //competition table update
        Competition UpdatedCompetition = Competition.builder()
                .competitionId(id)
                .user(competition.getUser())
                .competitionName(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .relatedUrl(request.getRelatedURL())
                .content(request.getCkData())
                .competitionStatus(competition.getCompetitionStatus())
                .createAt(competition.getCreateAt())
                .updateAt(LocalDateTime.now())
                .build();
        competitionJpa.save(UpdatedCompetition);


        // competition_img table update
        // 기존 ck 이미지 데이터에서 삭제된 이미지 데이터 삭제
        if (!request.getDeletedCkImgUrls().isEmpty()) {
            List<CompetitionImg> deletedCompetitionImgs = competitionImgs.stream().filter((item) -> request.getDeletedCkImgUrls().contains(item.getImgUrl())).toList();
            competitionImgJpa.deleteAll(deletedCompetitionImgs);
            storageService.uploadCancel(deletedCompetitionImgs.stream().map(CompetitionImg::getImgUrl).collect(Collectors.toList()));
        }

        // 새로운 ck 이미지 데이터 저장
        List<CompetitionImg> newCompetitionImgs = request.getRealCkImgs().stream().map((r) ->
                CompetitionImg.builder()
                        .competition(competition)
                        .imgUrl(r)
                        .fileName(r)
                        .build()
        ).toList();
        competitionImgJpa.saveAll(newCompetitionImgs);


        // competition_place table update
        List<Integer> NotDeletedPlaceIds = request.getUpdatePlaces().stream().map(UpdatePlace::getCompetitionPlaceId).filter(Objects::nonNull).collect(Collectors.toList());
        if (NotDeletedPlaceIds.isEmpty()) {
            competitionPlaceJpa.deleteAllByCompetition(competition);
        }else {
            competitionPlaceJpa.deleteAllExclusivePlaceId(competition, NotDeletedPlaceIds);
        }
        request.getUpdatePlaces().forEach((place -> {
            if (place.getCompetitionPlaceId() == null) {
                competitionPlaceJpa.save(CompetitionPlace.builder()
                        .competition(competition)
                        .placeName(place.getPlaceName())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .address(place.getAddress())
                        .build());
            }
        }));


        // competition_attached_file table update
        List<CompetitionAttachedFile> deletedAttachedFiles = competitionAttachedFiles.stream().filter((af) -> !request.getUploadedAttachedFiles().contains(af.getFilePath())).toList();
        competitionAttachedFileJpa.deleteAll(deletedAttachedFiles);
        if (files != null && !files.isEmpty()) {
            List<FileDto> response = storageService.fileUploadAndGetUrl(files, type.orElseGet(()-> SaveFileType.small));
            List<CompetitionAttachedFile> newCompetitionAttachedFiles = response.stream()
                    .map((res) -> CompetitionAttachedFile.builder()
                            .competition(competition)
                            .filePath(res.getFileUrl())
                            .fileName(res.getFileName())
                            .build()).toList();
            competitionAttachedFileJpa.saveAll(newCompetitionAttachedFiles);
        }


        // division table update
        List<Division> deletedDivisions = divisions.stream().filter((d) -> !request.getDivisions().contains(d.getDivisionName())).toList();
        deletedDivisions.forEach(item -> {if(competitionRecordJpa.existsAllByDivision(item)) throw new ConflictException("해당 종별은 결과가 이미 입력되어 있어 삭제가 불가능합니다.", item.getDivisionName());});
        divisionJpa.deleteAll(deletedDivisions);

        List<String> notUpdatedDivisionNames = request.getDivisions().stream().filter(d -> divisionJpa.existsByDivisionNameAndCompetition(d, competition)).toList();
        List<String> newDivisionNames = request.getDivisions().stream().filter((item) -> !notUpdatedDivisionNames.contains(item)).toList();
        List<Division> newDivisions = newDivisionNames.stream().map((divisionName) ->
                Division.builder()
                        .competition(competition)
                        .divisionName(divisionName)
                        .build()
        ).toList();
        divisionJpa.saveAll(newDivisions);

        return competition.getCompetitionId();
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
            storageService.uploadCancel(deleteFiles);
        }
        divisionJpa.deleteAll(divisions);
        competitionImgJpa.deleteAll(competitionImgs);
        competitionPlaceJpa.deleteAll(competitionPlaces);
        competitionAttachedFileJpa.deleteAll(competitionAttachedFiles);
        competitionJpa.delete(competition);

        return "OK";
    }
}
