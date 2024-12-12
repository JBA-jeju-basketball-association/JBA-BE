package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionParticipationService;
import github.com.jbabe.service.storage.ServerDiskService;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.participate.ModifyParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;
import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/api/competition/participate")
@RequiredArgsConstructor
@Tag(name = "대회 참가 관련 API", description = "대회 참가 관련 API")
public class CompetitionParticipationController {
    private final CompetitionParticipationService competitionParticipationService;
    private final ServerDiskService serverDiskService;

    @Operation(summary = "대회 참가 신청", description = "대회 참가 신청을 합니다. 요청 성공후 반환값의 data는 새로 만들어진 로우값의 pk(고유번호) 입니다.")
    @PostMapping(value = "/{divisionId}",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto participateCompetition(@PathVariable Long divisionId, @RequestPart("body") @Valid ParticipateRequest participateRequest,
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (files != null && !files.isEmpty()) {
            List<FileDto> fileDtoList = serverDiskService.fileUploadAndGetUrl(files);
            participateRequest.setFiles(fileDtoList);
        }

        return new ResponseDto(HttpStatus.CREATED).setCreateData(competitionParticipationService.applicationForParticipationInCompetition(divisionId, participateRequest, customUserDetails));
    }

    @Operation(summary = "내가 참가한 대회 조회(신청 기록 조회)", description = "내가 참가한 대회를 조회합니다.")
    @GetMapping("/my")
    public ResponseDto getMyParticipate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String cursor,
                                        @RequestParam(required = false) Long idCursor) {
        SearchRequest searchRequest = SearchRequest.fromSize(size);
        return new ResponseDto(competitionParticipationService.getMyParticipate(customUserDetails, searchRequest.withIdAndCursor(idCursor, cursor)));
    }
    @Operation(summary = "대회 참가 신청 취소(삭제)", description = "대회 참가 신청을 취소합니다.")
    @DeleteMapping("/{participationCompetitionId}")
    public ResponseDto deleteParticipate(@PathVariable Long participationCompetitionId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        competitionParticipationService.deleteParticipate(participationCompetitionId, customUserDetails);
        return new ResponseDto();
    }

    @Operation(summary = "대회 참가 신청 수정", description = "대회 참가 신청을 수정합니다.")
    @PutMapping(value = "/{participationCompetitionId}",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto updateParticipate(@PathVariable Long participationCompetitionId,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                         @RequestPart("body") @Valid ModifyParticipateRequest modifyParticipateRequest) {

        if (files != null && !files.isEmpty()) {
            List<FileDto> newFileDtoList = serverDiskService.fileUploadAndGetUrl(files);
            newFileDtoList.addAll(modifyParticipateRequest.getRemainingFiles());
            modifyParticipateRequest.setFiles(newFileDtoList);
        }
        competitionParticipationService.updateParticipate(participationCompetitionId, customUserDetails, modifyParticipateRequest);
        return new ResponseDto(participationCompetitionId);
    }
    @Operation(summary = "대회 참가 신청 상세 조회", description = "대회 참가 신청 상세 정보를 조회합니다.")
    @GetMapping(value = "/{participationCompetitionId}")
    public ResponseDto getParticipateDetail(@PathVariable Long participationCompetitionId) {
        return new ResponseDto(competitionParticipationService.getMyParticipateById(participationCompetitionId));
    }

}
