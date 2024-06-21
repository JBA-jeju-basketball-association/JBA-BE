package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionResultService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.AddCompetitionResultFinal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class CompetitionResultController {
    private final CompetitionResultService competitionResultService;

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
        return new ResponseDto(competitionResultService.addCompetitionResult(id,  request.getRequests()));
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
        return new ResponseDto(competitionResultService.getCompetitionResult(id));
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
        return new ResponseDto(competitionResultService.getCompetitionResultWithTitle(id));
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
        return new ResponseDto(competitionResultService.updateCompetitionResult(id, request.getRequests()));
    }
}
