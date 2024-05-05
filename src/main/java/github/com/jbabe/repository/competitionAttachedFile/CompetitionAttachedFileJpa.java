package github.com.jbabe.repository.competitionAttachedFile;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionAttachedFileJpa extends JpaRepository<CompetitionAttachedFile, Integer> {
    List<CompetitionAttachedFile> findAllByCompetition(Competition competition);
}
