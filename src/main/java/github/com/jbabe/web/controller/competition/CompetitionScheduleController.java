package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionScheduleService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.GetScheduleResponse;
import github.com.jbabe.web.dto.competition.PostCompetitionScheduleRequest;
import github.com.jbabe.web.dto.competition.PostCompetitionScheduleRequestBox;
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
public class CompetitionScheduleController {
    private final CompetitionScheduleService competitionScheduleService;

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
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"경기 번호가 없습니다.\",\n \"request\": \"gameNumber\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"장소를 입력해주세요.\",\n \"request\": \"place\"\n}"),
                                    @ExampleObject(name = "시작일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"5대5 경기여부를 입력해주세요.\",\n \"request\": \"is5x5\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @PostMapping("/post/schedule/{id}")
    public ResponseDto postCompetitionSchedule (@RequestBody @Valid PostCompetitionScheduleRequestBox request,
                                                @PathVariable Integer id) {
        String response = competitionScheduleService.postCompetitionSchedule(request.getRequest(), id);
        return new ResponseDto(response);
    }


    @Operation(summary = "대회일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}"),
                                    @ExampleObject(name = "대회일정을 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회일정을 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                            }
                    ))
    })
    @GetMapping("/schedule/{id}")
    public ResponseDto getCompetitionSchedule (@PathVariable Integer id) {
        List<GetScheduleResponse> res = competitionScheduleService.getCompetitionSchedule(id);
        return new ResponseDto(res);
    }
}
