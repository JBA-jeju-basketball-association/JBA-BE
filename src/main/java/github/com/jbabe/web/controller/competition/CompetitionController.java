package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

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
    @PostMapping("/add-competition-info")
    public ResponseDto addCompetition(@RequestPart("requestData") @Valid AddCompetitionRequest addCompetitionRequest,
                                      @RequestPart(value = "requestFiles",required = false)List<MultipartFile> files,
                                      @RequestParam(required = false) Optional<SaveFileType> type,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String competitionName = competitionService.addCompetitionInfo(addCompetitionRequest, files, type,customUserDetails);
        return new ResponseDto(competitionName);
    }

    @Operation(summary = "대회정보 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "status 입력 오류", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"상태를 정확히 입력해주세요\",\n \"request\": \"alsdw\"\n}"),
                                    @ExampleObject(name = "년도 입력 오류", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"년도를 정확히 입력해주세요\",\n \"request\": \"12\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회정보 LIST의 총 갯수가 0개", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"totalElement is 0\",\n \"request\": \"\"\n}")
                    ))
    })
    @GetMapping("/competition")
    public ResponseDto getCompetitionList(@RequestParam("status") String status,
                                          @RequestParam("year") String year,
                                          Pageable pageable) {
        if (status == null || (!status.equals("ALL") && !status.equals("EXPECTED") && !status.equals("PROCEEDING") && !status.equals("COMPLETE"))) throw new BadRequestException("상태를 정확히 입력해주세요", status);
        if (year == null || year.isEmpty()) throw new BadRequestException("년도를 정확히 입력해주세요", year);
        Page<CompetitionListResponse> data = competitionService.getCompetitionList(status, year, pageable);
    return new ResponseDto(data);
    }


    @Operation(summary = "대회정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 아이디와 일치하는 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @GetMapping("/detail/{id}")
    public ResponseDto getCompetitionDetail(@PathVariable Integer id) {
        CompetitionDetailResponse data = competitionService.getCompetitionDetail(id);
        return new ResponseDto(data);
    }

    @Operation(summary = "년도 LIST 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": null\n}")
                    ))
    })
    @GetMapping("/find-year-list")
    public ResponseDto getCompetitionYearList() {
        return new ResponseDto(competitionService.getCompetitionYearList());
    }

    @Operation(summary = "대회결과 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "스테이지 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"스테이지를 입력해주세요.\",\n \"request\": \"floor\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 입력해주세요.\",\n \"request\": \"division\"\n}"),
                                    @ExampleObject(name = "홈팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"HOME 팀명을 입력해주세요.\",\n \"request\": \"homeName\"\n}"),
                                    @ExampleObject(name = "어웨이팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"AWAY 팀명을 입력해주세요.\",\n \"request\": \"awayName\"\n}"),
                                    @ExampleObject(name = "홈팀 점수 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수를 입력해주세요.\",\n \"request\": \"homeScore\"\n}"),
                                    @ExampleObject(name = "홈팀 점수 0 미만", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 0점 이상입니다.\",\n \"request\": \"homeScore\"\n}"),
                                    @ExampleObject(name = "홈팀 점수 200 초과", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 200점 이하입니다.\",\n \"request\": \"homeScore\"\n}"),
                                    @ExampleObject(name = "어웨이팀 점수 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수를 입력해주세요.\",\n \"request\": \"awayScore\"\n}"),
                                    @ExampleObject(name = "어웨이팀 점수 0 미만", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 0점 이상입니다.\",\n \"request\": \"awayScore\"\n}"),
                                    @ExampleObject(name = "어웨이팀 점수 200 초과", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 200점 이하입니다.\",\n \"request\": \"awayScore\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @PostMapping("/add-result/{id}")
    public ResponseDto addCompetitionResult(@PathVariable Integer id,
                                            @RequestBody @Valid AddCompetitionResultFinal request) {
        return new ResponseDto(competitionService.addCompetitionResult(id,  request.getRequests()));
    }

    @Operation(summary = "대회결과 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @GetMapping("/result")
    public ResponseDto getCompetitionResult(@RequestParam("id") Integer id) {
        return new ResponseDto(competitionService.getCompetitionResult(id));
    }

    @Operation(summary = "대회결과 조회(대회결과 수정전 get요청)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @GetMapping("/result-with-title")
    public ResponseDto getCompetitionResultWithTitle(@RequestParam("id") Integer id) {
        return new ResponseDto(competitionService.getCompetitionResultWithTitle(id));
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
    @PostMapping("/update-competition-info/{id}")
    public ResponseDto updateCompetition(@PathVariable Integer id,
                                         @RequestPart("requestData") @Valid UpdateCompetitionRequest updateCompetitionRequest,
                                         @RequestPart(value = "requestFiles",required = false)List<MultipartFile> files,
                                         @RequestParam(required = false) Optional<SaveFileType> type) {
        return new ResponseDto(competitionService.updateCompetition(id, updateCompetitionRequest, files, type));
    }

    @Operation(summary = "대회결과 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "스테이지 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"스테이지를 입력해주세요.\",\n \"request\": \"floor\"\n}"),
                                    @ExampleObject(name = "종별 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"종별을 입력해주세요.\",\n \"request\": \"division\"\n}"),
                                    @ExampleObject(name = "홈팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"HOME 팀명을 입력해주세요.\",\n \"request\": \"homeName\"\n}"),
                                    @ExampleObject(name = "어웨이팀명 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"AWAY 팀명을 입력해주세요.\",\n \"request\": \"awayName\"\n}"),
                                    @ExampleObject(name = "홈팀 점수 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수를 입력해주세요.\",\n \"request\": \"homeScore\"\n}"),
                                    @ExampleObject(name = "홈팀 점수 0 미만", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 0점 이상입니다.\",\n \"request\": \"homeScore\"\n}"),
                                    @ExampleObject(name = "홈팀 점수 200 초과", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 200점 이하입니다.\",\n \"request\": \"homeScore\"\n}"),
                                    @ExampleObject(name = "어웨이팀 점수 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수를 입력해주세요.\",\n \"request\": \"awayScore\"\n}"),
                                    @ExampleObject(name = "어웨이팀 점수 0 미만", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 0점 이상입니다.\",\n \"request\": \"awayScore\"\n}"),
                                    @ExampleObject(name = "어웨이팀 점수 200 초과", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"점수는 200점 이하입니다.\",\n \"request\": \"awayScore\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"시작일을 입력해주세요.\",\n \"request\": \"startDate\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @PutMapping("/update-result/{id}")
    public ResponseDto updateCompetitionResult(@PathVariable Integer id,
                                               @RequestBody @Valid AddCompetitionResultFinal request) {
        return new ResponseDto(competitionService.updateCompetitionResult(id, request.getRequests()));
    }

    @Operation(summary = "대회 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseDto deleteCompetition(@PathVariable Integer id) {
        return new ResponseDto(competitionService.deleteCompetition(id));
    }
}


