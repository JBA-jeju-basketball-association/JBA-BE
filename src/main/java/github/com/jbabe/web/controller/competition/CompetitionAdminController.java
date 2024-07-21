package github.com.jbabe.web.controller.competition;

import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.repository.division.DivisionEnumJpa;
import github.com.jbabe.service.competition.CompetitionAdminService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.GetCompetitionAdminListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class CompetitionAdminController {
    private final CompetitionAdminService competitionAdminService;
    private final DivisionEnumJpa divisionEnumJpa;

    @Operation(summary = "대회 어드민 리스트 조회", description =
            """
            searchType 은 title, email, id 만 가능 \n
            searchKey 는 string 타입으로 null 또는 빈 스트링 넣으면 전체 조회 \n
            날짜는 둘중 하나가 null 일 경우 전체조회(filterStartDate, filterEndDate 둘다 있어야 필터가 적용됨) \n
            division 은 전체 또는 divisionENum table 에 있는 division 만 넣을 수 있음(대회 게시물 전체 조회 및 division 목록 조회 api 참고) \n
            situation 은 전체, 예정, 진행중, 종료 만 가능
            
            searchKey, filterStartDate, filterEndDate 만 null 허용
            searchKey 미입력 시 title
            division 미입력 시 전체
            situation 미입력 시 전체
            page 미입력 시 0
            size 미입력 시 20
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "searchType 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"searchType 은 title, email, id 만 가능합니다.\",\n \"request\": \"전체\"\n}"),
                                    @ExampleObject(name = "division 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"해당 종별을 존재하지 않습니다.\",\n \"request\": \"넥스트레벨\"\n}"),
                                    @ExampleObject(name = "situation 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"situation은 전체, 예정, 진행중, 완료 만 가능합니다.\",\n \"request\": \"완료예정\"\n}"),
                            }
                    ))
    })
    @GetMapping("/admin/list")
    public ResponseDto getCompetitionAdminList(@RequestParam(defaultValue = "title") String searchType,
                                               @RequestParam(required = false) String searchKey,
                                               @RequestParam(required = false) LocalDate filterStartDate,
                                               @RequestParam(required = false) LocalDate filterEndDate,
                                               @RequestParam(defaultValue = "전체") String division,
                                               @RequestParam(defaultValue = "전체") String situation,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<String> searchTypeList = new ArrayList<>(Arrays.asList("title", "email", "id"));
        if (!searchTypeList.contains(searchType))
            throw new BadRequestException("searchType 은 title, email, id 만 가능합니다.", searchType);
        List<String> divisionList = new ArrayList<>(divisionEnumJpa.findAll().stream().map(DivisionEnum::getDivisionName).toList());
        divisionList.add("전체");
        if (!divisionList.contains(division))
            throw new BadRequestException("해당 종별을 존재하지 않습니다.", division);

        List<String> situationList = new ArrayList<>(Arrays.asList("전체", "예정", "진행중", "종료"));
        if (!situationList.contains(situation))
            throw new BadRequestException("situation은 전체, 예정, 진행중, 종료 만 가능합니다.", situation);

        Page<GetCompetitionAdminListResponse> responses = competitionAdminService.getCompetitionAdminList(searchType, searchKey, filterStartDate, filterEndDate, division, situation, pageable);
        return new ResponseDto(responses);
    }

    @Operation(summary = "등록된 대회 게시물 총 갯수 및 종별 목록 조회")
    @GetMapping("/admin/total-competition-and-division-list")
    public ResponseDto getTotalCompetitionAndDivisionList() {
        return new ResponseDto(competitionAdminService.getTotalCompetitionAndDivisionList());
    }

    @Operation(summary = "대회 삭제", description = "대회 정보 및 일정(결과)까지 모두 삭제됨")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "대회를 찾을 수 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"해당 id로 대회를 찾을 수 없습니다.\",\n \"request\": \"1\"\n}")
                    ))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseDto deleteCompetition(@PathVariable Integer id) {
        return new ResponseDto(competitionAdminService.deleteCompetition(id));
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
    @DeleteMapping("/delete/schedule/{id}")
    public ResponseDto deleteCompetitionSchedule (@PathVariable Integer id) {
        String res = competitionAdminService.deleteCompetitionSchedule(id);
        return new ResponseDto(res);
    }
}
