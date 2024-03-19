package github.com.jbabe.repository.competitionAttachedFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionAttachedFileJpa extends JpaRepository<CompetitionAttachedFile, Integer> {
}
