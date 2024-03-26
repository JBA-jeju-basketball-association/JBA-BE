package github.com.jbabe.repository.competition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionJpa extends JpaRepository<Competition, Integer> {
}
