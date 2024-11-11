package github.com.jbabe.web.controller.competition;

import github.com.jbabe.repository.division.DivisionEnumJpa;
import github.com.jbabe.service.competition.CompetitionAdminService;
import github.com.jbabe.service.competition.CompetitionDetailService;
import github.com.jbabe.service.competition.CompetitionResultService;
import github.com.jbabe.service.competition.CompetitionScheduleService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.AddCompetitionRequest;
import github.com.jbabe.web.dto.competition.PostCompetitionScheduleRequestBox;
import github.com.jbabe.web.dto.competition.PostResultRequestBox;
import github.com.jbabe.web.dto.competition.UpdateCompetitionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/admin/competition")
@RequiredArgsConstructor
public class CompetitionAdminCUDController {
    private final CompetitionAdminService competitionAdminService;
    private final CompetitionScheduleService competitionScheduleService;
    private final CompetitionDetailService competitionDetailService;
    private final CompetitionResultService competitionResultService;


    //대회정보 등록, 수정, 삭제
    @Operation(summary = "대회정보 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid_Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "제목 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"제목을 입력해주세요.\",\n \"request\": \"title\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 선택해주세요.\",\n \"request\": \"divisions\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                                    @ExampleObject(name = "종료일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종료일을 입력해주세요.\",\n \"request\": \"endDate\"\n}"),
                                    @ExampleObject(name = "장소 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"장소를 등록해주세요.\",\n \"request\": \"places\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "유저를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"user을 찾을 수 없습니다.\",\n \"request\": 10\n}")
                    ))
    })
    @PostMapping
    public ResponseDto addCompetition(@RequestPart("requestData") @Valid AddCompetitionRequest addCompetitionRequest,
                                      @RequestPart(value = "requestFiles",required = false) List<MultipartFile> files,
                                      @RequestParam(required = false) Optional<SaveFileType> type,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String competitionName = competitionDetailService.addCompetitionInfo(addCompetitionRequest, files, type,customUserDetails);
        return new ResponseDto(competitionName);
    }

    @Operation(summary = "대회정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid_Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "제목 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"제목을 입력해주세요.\",\n \"request\": \"title\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 선택해주세요.\",\n \"request\": \"divisions\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                                    @ExampleObject(name = "종료일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종료일을 입력해주세요.\",\n \"request\": \"endDate\"\n}"),
                                    @ExampleObject(name = "장소 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"장소를 등록해주세요.\",\n \"request\": \"places\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "유저를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"user을 찾을 수 없습니다.\",\n \"request\": 10\n}")
                    )),
            @ApiResponse(responseCode = "409", description = "CONFLICT",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "이미 결과에 입력된 종별이라 삭제 불가", value = "{\n  \"code\": 409,\n  \"message\": \"CONFLICT\",\n \"detailMessage\": \"해당 종별은 결과가 이미 입력되어 있어 삭제가 불가능합니다.\",\n \"request\": \"element\"\n}")
                    ))
    })
    @PostMapping("/{id}")
    public ResponseDto updateCompetition(@PathVariable Integer id,
                                         @RequestPart("requestData") @Valid UpdateCompetitionRequest updateCompetitionRequest,
                                         @RequestPart(value = "requestFiles",required = false)List<MultipartFile> files,
                                         @RequestParam(required = false) Optional<SaveFileType> type) {
        return new ResponseDto(competitionDetailService.updateCompetition(id, updateCompetitionRequest, files, type));
    }

    @Operation(summary = "대회 삭제", description = "대회 정보 및 일정(결과)까지 모두 삭제됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @DeleteMapping("/{id}")
    public ResponseDto deleteCompetition(@PathVariable Integer id) {
        return new ResponseDto(competitionAdminService.deleteCompetition(id));
    }



    //일정 등록 수정 삭제
    @Operation(summary = "대회일정 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "스테이지 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"스테이지를 입력해주세요.\",\n \"request\": \"floor\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 입력해주세요.\",\n \"request\": \"division\"\n}"),
                                    @ExampleObject(name = "홈팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"HOME 팀명을 입력해주세요.\",\n \"request\": \"homeName\"\n}"),
                                    @ExampleObject(name = "어웨이팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"AWAY 팀명을 입력해주세요.\",\n \"request\": \"awayName\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                                    @ExampleObject(name = "경기번호 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"경기 번호가 없습니다.\",\n \"request\": \"gameNumber\"\n}"),
                                    @ExampleObject(name = "장소 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"장소를 입력해주세요.\",\n \"request\": \"place\"\n}"),
                                    @ExampleObject(name = "5대5 경기여부 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"5대5 경기여부를 입력해주세요.\",\n \"request\": \"is5x5\"\n}"),
                                    @ExampleObject(name = "이미 일정이 등록된 대회", value = "{\n  \"code\": 400,\n  \"message\": \"Bad_Request\",\n \"detailMessage\": \"이미 일정이 등록된 대회입니다.\",\n \"request\": \"1\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @PostMapping("/{id}/schedule")
    public ResponseDto postCompetitionSchedule (@RequestBody @Valid PostCompetitionScheduleRequestBox request,
                                                @PathVariable Integer id) {
        String response = competitionScheduleService.postCompetitionSchedule(request.getRequest(), id);
        return new ResponseDto(response);
    }

    @Operation(summary = "대회일정 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "스테이지 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"스테이지를 입력해주세요.\",\n \"request\": \"floor\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 입력해주세요.\",\n \"request\": \"division\"\n}"),
                                    @ExampleObject(name = "홈팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"HOME 팀명을 입력해주세요.\",\n \"request\": \"homeName\"\n}"),
                                    @ExampleObject(name = "어웨이팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"AWAY 팀명을 입력해주세요.\",\n \"request\": \"awayName\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"경기 번호가 없습니다.\",\n \"request\": \"gameNumber\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"장소를 입력해주세요.\",\n \"request\": \"place\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"5대5 경기여부를 입력해주세요.\",\n \"request\": \"is5x5\"\n}"),
                                    @ExampleObject(name = "일정등록 단계가 아님", value = "{\n  \"code\": 400,\n  \"message\": \"Bad_Request\",\n \"detailMessage\": \"일정등록 단계가 아닙니다.\",\n \"request\": \"1\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @PutMapping("/{id}/schedule")
    public ResponseDto updateCompetitionSchedule (@PathVariable Integer id,
                                                  @RequestBody @Valid PostCompetitionScheduleRequestBox request) {
        String res = competitionScheduleService.updateCompetitionSchedule(id, request.getRequest());
        return new ResponseDto(res);
    }

    @Operation(summary = "대회일정(결과) 삭제", description = "대회 정보는 삭제되지 않고 일정(결과)만 삭제됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}"),
                            }
                    ))
    })
    @DeleteMapping("/{id}/schedule")
    public ResponseDto deleteCompetitionSchedule (@PathVariable Integer id) {
        String res = competitionAdminService.deleteCompetitionSchedule(id);
        return new ResponseDto(res);
    }

    //대회결과
    @Operation(summary = "대회결과 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "스테이지 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"스테이지를 입력해주세요.\",\n \"request\": \"floor\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 입력해주세요.\",\n \"request\": \"division\"\n}"),
                                    @ExampleObject(name = "홈팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"HOME 팀명을 입력해주세요.\",\n \"request\": \"homeName\"\n}"),
                                    @ExampleObject(name = "어웨이팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"AWAY 팀명을 입력해주세요.\",\n \"request\": \"awayName\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                                    @ExampleObject(name = "경기번호 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"경기 번호가 없습니다.\",\n \"request\": \"gameNumber\"\n}"),
                                    @ExampleObject(name = "장소 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"장소를 입력해주세요.\",\n \"request\": \"place\"\n}"),
                                    @ExampleObject(name = "5대5 경기여부 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"5대5 경기여부를 입력해주세요.\",\n \"request\": \"is5x5\"\n}"),
                                    @ExampleObject(name = "일정이 등록 되어있지 않음", value = "{\n  \"code\": 400,\n  \"message\": \"Bad_Request\",\n \"detailMessage\": \"일정 먼저 입력 바랍니다.\",\n \"request\": \"1\"\n}"),
                                    @ExampleObject(name = "점수가 0점 미만", value = "{\n  \"code\": 400,\n  \"message\": \"Bad_Request\",\n \"detailMessage\": \"점수는 0점 이상입니다.\",\n \"request\": \"1\"\n}"),
                                    @ExampleObject(name = "점수가 200점 초과", value = "{\n  \"code\": 400,\n  \"message\": \"Bad_Request\",\n \"detailMessage\": \"점수는 200점 이하입니다.\",\n \"request\": \"1\"\n}"),
                                    @ExampleObject(name = "점수 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Bad_Request\",\n \"detailMessage\": \"점수를 입력해주세요.\",\n \"request\": \"1\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @PostMapping("/{id}/result")
    public ResponseDto postCompetitionResult (@PathVariable Integer id,
                                              @RequestBody @Valid PostResultRequestBox request) {

        String res = competitionResultService.postCompetitionResult(id, request.getRequests());
        return new ResponseDto(res);
    }
}
