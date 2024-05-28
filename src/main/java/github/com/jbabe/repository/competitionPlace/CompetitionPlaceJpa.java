package github.com.jbabe.repository.competitionPlace;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionPlaceJpa extends JpaRepository<CompetitionPlace, Integer> {
    List<CompetitionPlace> findAllByCompetition(Competition competition);

    @Modifying
    @Query("DELETE  " +
            "FROM CompetitionPlace p " +
            "WHERE p.competition = :competition AND p.competitionPlaceId NOT IN :NotDeletedPlaceIds ")
    void deleteAllExclusivePlaceId(Competition competition, List<Integer> NotDeletedPlaceIds);

    void deleteAllByCompetition(Competition competition);
}
