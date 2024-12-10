package github.com.jbabe.repository.division;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionJpa extends JpaRepository<Division, Integer>, DivisionJpaCustom {
    List<Division> findAllByCompetition(Competition competition);

    List<Division> findAllByCompetitionCompetitionId(Integer id);
    boolean existsByDivisionNameAndCompetition(String divisionName, Competition competition);
}
