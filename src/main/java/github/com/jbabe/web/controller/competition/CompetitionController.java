package github.com.jbabe.web.controller.competition;

import github.com.jbabe.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/competition")
@RequiredArgsConstructor
public class CompetitionController {
    @PostMapping("/add")
    public ResponseDto addCompetition() {

    }

}
