package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionResultService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.AddCompetitionResultFinal;
import github.com.jbabe.web.dto.competition.GetResultResponse;
import github.com.jbabe.web.dto.competition.PostResultRequestBox;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/post/result/{id}")
    public ResponseDto postCompetitionResult (@PathVariable Integer id,
                                              @RequestBody @Valid PostResultRequestBox request) {

        String res = competitionResultService.postCompetitionResult(id, request.getRequests());
        return new ResponseDto(res);
    }

    @Operation(summary = "대회결과 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}"),
                            }
                    ))
    })
    @GetMapping("/result/{id}")
    public ResponseDto getCompetitionResult (@PathVariable Integer id) {
        List<GetResultResponse> res = competitionResultService.getCompetitionResult(id);
        return new ResponseDto(res);
    }
}
