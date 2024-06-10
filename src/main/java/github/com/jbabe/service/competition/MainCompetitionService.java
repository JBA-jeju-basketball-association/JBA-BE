package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionPlace.CompetitionPlace;
import github.com.jbabe.repository.competitionPlace.CompetitionPlaceJpa;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.competition.MainCompetitionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainCompetitionService {

    private final CompetitionJpa competitionJpa;
    private final CompetitionPlaceJpa competitionPlaceJpa;

    public List<MainCompetitionResponse> getCompetitionInfoInMain() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "startDate"));
        Page<Competition> competitionPage = competitionJpa.findAllByCompetitionStatus(Competition.CompetitionStatus.NORMAL, pageable);
        List<Competition> competitions = competitionPage.getContent();

        List<MainCompetitionResponse> responses = competitions.stream().map(c -> {
            List<CompetitionPlace> competitionPlaces = competitionPlaceJpa.findAllByCompetition(c);
            List<String> placeNames = competitionPlaces.stream().map(CompetitionPlace::getPlaceName).toList();
            return MainCompetitionResponse.builder()
                    .competitionId(c.getCompetitionId())
                    .title(c.getCompetitionName())
                    .startDate(c.getStartDate())
                    .endDate(c.getEndDate())
                    .places(placeNames)
                    .build();
        }).toList();

        return responses;
    }
}
