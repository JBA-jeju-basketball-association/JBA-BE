package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionParticipationService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
@Tag(name = "대회 참가 관련 API", description = "대회 참가 관련 API")
public class CompetitionParticipationController {
    private final CompetitionParticipationService competitionParticipationService;

    @Operation(summary = "대회 참가 신청", description = "대회 참가 신청을 합니다. 요청 성공후 반환값의 data는 새로 만들어진 로우값의 pk(고유번호) 입니다.")
    @PostMapping("/{divisionId}/participate")
    public ResponseDto participateCompetition(@PathVariable Long divisionId, @RequestBody @Valid ParticipateRequest participateRequest,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return new ResponseDto(HttpStatus.CREATED).setCreateData(competitionParticipationService.applicationForParticipationInCompetition(divisionId, participateRequest, customUserDetails));
    }
    @Operation(summary = "내가 참가한 대회 조회(신청 기록 조회)", description = "내가 참가한 대회를 조회합니다. <br>" +
            "isAll이 true일 경우 참가신청한 기록을 전부 조회합니다. false거나 값이 쿼리파라미터가 없을경우 컴페티션테이블의 participationStartDate와 participationEndDate 사이에 있는 요청기록만 보여줍니다.")
    @GetMapping("/my-participate")
    public ResponseDto getMyParticipate(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(required = false) boolean isAll) {
        return new ResponseDto(competitionParticipationService.getMyParticipate(customUserDetails, isAll));
    }
}
