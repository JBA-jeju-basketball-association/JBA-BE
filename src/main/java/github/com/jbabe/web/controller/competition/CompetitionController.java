package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.AddCompetitionRequest;
import github.com.jbabe.web.dto.competition.AddCompetitionResultRequest;
import github.com.jbabe.web.dto.competition.CompetitionDetailResponse;
import github.com.jbabe.web.dto.competition.CompetitionListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @PostMapping("/add-competition-info")
    public ResponseDto addCompetition(@RequestPart("requestData") @Valid AddCompetitionRequest addCompetitionRequest,
                                      @RequestPart(value = "requestFiles",required = false)List<MultipartFile> files,
                                      @RequestParam(required = false) Optional<SaveFileType> type,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String competitionName = competitionService.addCompetitionInfo(addCompetitionRequest, files, type,customUserDetails);
        return new ResponseDto(competitionName);
    }

    @GetMapping("/competition")
    public ResponseDto getCompetitionList(@RequestParam("status") String status,
                                          @RequestParam("year") String year,
                                          Pageable pageable) {
        Page<CompetitionListResponse> data = competitionService.getCompetitionList(status, year, pageable);
    return new ResponseDto(data);
    }

    @GetMapping("/detail/{id}")
    public ResponseDto getCompetitionDetail(@PathVariable Integer id) {
        CompetitionDetailResponse data = competitionService.getCompetitionDetail(id);
        return new ResponseDto(data);
    }

    @GetMapping("/find-year-list")
    public ResponseDto getCompetitionYearList() {
        return new ResponseDto(competitionService.getCompetitionYearList());
    }

    @PostMapping("/add-result/{id}")
    public ResponseDto addCompetitionResult(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable Integer id,
                                            @RequestBody @Valid List<AddCompetitionResultRequest> request) {
        return new ResponseDto(competitionService.addCompetitionResult(customUserDetails, id,  request));
    }
}


