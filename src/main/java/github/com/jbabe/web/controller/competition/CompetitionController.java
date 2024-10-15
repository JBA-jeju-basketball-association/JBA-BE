package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionDetailService;
import github.com.jbabe.service.competition.CompetitionResultService;
import github.com.jbabe.service.competition.CompetitionScheduleService;
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
    private final CompetitionDetailService competitionDetailService;
    private final CompetitionResultService competitionResultService;
    private final CompetitionScheduleService competitionScheduleService;


    //목록조회
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
    @GetMapping
    public ResponseDto getCompetitionList(@RequestParam("status") String status,
                                          @RequestParam(value = "year", required = false) String year,
                                          Pageable pageable) {
        if (status == null || (!status.equals("ALL") && !status.equals("EXPECTED") && !status.equals("PROCEEDING") && !status.equals("COMPLETE"))) throw new BadRequestException("상태를 정확히 입력해주세요", status);
        Page<CompetitionListResponse> data = competitionService.getCompetitionList(status, year, pageable);
    return new ResponseDto(data);
    }

    @Operation(summary = "년도 LIST 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회를 찾을 수 없습니다.\",\n \"request\": null\n}")
                    ))
    })
    @GetMapping("/years")
    public ResponseDto getCompetitionYearList() {
        return new ResponseDto(competitionService.getCompetitionYearList());
    }

    @Operation(summary = "종별 LIST 조회")
    @GetMapping("/divisions")
    public ResponseDto getCompetitionDivisionList() {
        return new ResponseDto(competitionService.getCompetitionDivisionList());
    }


    //상세조회
    @Operation(summary = "대회정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 아이디와 일치하는 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}"),
                                    @ExampleObject(name = "삭제되거나 숨겨진 대회", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"대회 조회가 불가능합니다.\",\n \"request\": \"DELETE OR HIDE\"\n}")
                            }
                    ))
    })
    @GetMapping("/{id}")
    public ResponseDto getCompetitionDetail(@PathVariable Integer id) {
        CompetitionDetailResponse data = competitionDetailService.getCompetitionDetail(id);
        return new ResponseDto(data);
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
    @GetMapping("/{id}/schedule")
    public ResponseDto getCompetitionSchedule (@PathVariable Integer id) {
        List<GetScheduleResponse> res = competitionScheduleService.getCompetitionSchedule(id);
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
    @GetMapping("/{id}/result")
    public ResponseDto getCompetitionResult (@PathVariable Integer id) {
        List<GetResultResponse> res = competitionResultService.getCompetitionResult(id);
        return new ResponseDto(res);
    }



}