package github.com.jbabe.web.controller.competition;

import github.com.jbabe.service.competition.MainCompetitionService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.MainCompetitionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class MainCompetitionController {
    private final MainCompetitionService mainCompetitionService;

    @GetMapping("/main")
    public ResponseDto getCompetitionInfoInMain() {
         List<MainCompetitionResponse> responses = mainCompetitionService.getCompetitionInfoInMain();
         return new ResponseDto(responses);
    }
}
