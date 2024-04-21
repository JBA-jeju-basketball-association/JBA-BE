package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.CompetitionService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.AddCompetitionRequest;
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

}
