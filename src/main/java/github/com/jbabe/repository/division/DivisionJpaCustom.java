package github.com.jbabe.repository.division;

import github.com.jbabe.repository.competition.Competition;

import java.util.Optional;

public interface DivisionJpaCustom {
    Optional<Integer> getDivisionIdByDivisionName(String divisionName);

    Optional<Competition> getCompetitionEntryDate(Integer divisionId);
    Optional<Competition> getCompetitionEntryDate(Long participationId);

    }
