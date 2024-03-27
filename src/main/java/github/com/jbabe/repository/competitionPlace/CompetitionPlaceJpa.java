package github.com.jbabe.repository.competitionPlace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionPlaceJpa extends JpaRepository<CompetitionPlace, Integer> {

}
