package github.com.jbabe.repository.competitionPlace;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionPlaceJpa extends JpaRepository<CompetitionPlace, Integer> {
    List<CompetitionPlace> findAllByCompetition(Competition competition);

}
