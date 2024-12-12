package github.com.jbabe.repository.division;

import github.com.jbabe.repository.competition.Competition;

import java.util.Optional;

public interface DivisionJpaCustom {
    Optional<Integer> getDivisionIdByDivisionName(String divisionName);

    Competition getCompetitionEntryDateByDivisionId(Long divisionId);
}
