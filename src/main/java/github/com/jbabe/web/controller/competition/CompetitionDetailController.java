package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionDetailService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.AddCompetitionRequest;
import github.com.jbabe.web.dto.competition.CompetitionDetailResponse;
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
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class CompetitionDetailController {
    private final CompetitionDetailService competitionDetailService;


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
    @PostMapping("/post/competition-info")
    public ResponseDto addCompetition(@RequestPart("requestData") @Valid AddCompetitionRequest addCompetitionRequest,
                                      @RequestPart(value = "requestFiles",required = false) List<MultipartFile> files,
                                      @RequestParam(required = false) Optional<SaveFileType> type,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String competitionName = competitionDetailService.addCompetitionInfo(addCompetitionRequest, files, type,customUserDetails);
        return new ResponseDto(competitionName);
    }



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
    @GetMapping("/detail/{id}")
    public ResponseDto getCompetitionDetail(@PathVariable Integer id) {
        CompetitionDetailResponse data = competitionDetailService.getCompetitionDetail(id);
        return new ResponseDto(data);
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
    @PostMapping("/update/competition-info/{id}")
    public ResponseDto updateCompetition(@PathVariable Integer id,
                                         @RequestPart("requestData") @Valid UpdateCompetitionRequest updateCompetitionRequest,
                                         @RequestPart(value = "requestFiles",required = false)List<MultipartFile> files,
                                         @RequestParam(required = false) Optional<SaveFileType> type) {
        return new ResponseDto(competitionDetailService.updateCompetition(id, updateCompetitionRequest, files, type));
    }


}
