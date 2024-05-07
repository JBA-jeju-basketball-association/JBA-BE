package github.com.jbabe.repository.division;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionJpa extends JpaRepository<Division, Integer> {
    List<Division> findAllByCompetition(Competition competition);

    Division findByCompetitionAndAndDivisionName(Competition competition, String divisionName);

}
