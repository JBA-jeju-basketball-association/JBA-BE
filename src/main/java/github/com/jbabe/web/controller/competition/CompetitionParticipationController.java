package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionParticipationService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/competition/participate")
@RequiredArgsConstructor
@Tag(name = "대회 참가 관련 API", description = "대회 참가 관련 API")
public class CompetitionParticipationController {
    private final CompetitionParticipationService competitionParticipationService;

    @Operation(summary = "대회 참가 신청", description = "대회 참가 신청을 합니다. 요청 성공후 반환값의 data는 새로 만들어진 로우값의 pk(고유번호) 입니다.")
    @PostMapping("/{divisionId}")
    public ResponseDto participateCompetition(@PathVariable Long divisionId, @RequestBody @Valid ParticipateRequest participateRequest,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
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
    @PutMapping("/{participationCompetitionId}")
    public ResponseDto updateParticipate(@PathVariable Long participationCompetitionId,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestBody @Valid ParticipateRequest participateRequest) {
        competitionParticipationService.updateParticipate(participationCompetitionId, customUserDetails, participateRequest);
        return new ResponseDto(participationCompetitionId);
    }
    @Operation(summary = "대회 참가 신청 상세 조회", description = "대회 참가 신청 상세 정보를 조회합니다.")
    @GetMapping("/{participationCompetitionId}")
    public ResponseDto getParticipateDetail(@PathVariable Long participationCompetitionId) {
        return new ResponseDto(competitionParticipationService.getMyParticipateById(participationCompetitionId));
    }

}
