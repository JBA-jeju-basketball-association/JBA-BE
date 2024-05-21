package github.com.jbabe.repository.competitionAttachedFile;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionAttachedFileJpa extends JpaRepository<CompetitionAttachedFile, Integer> {
    List<CompetitionAttachedFile> findAllByCompetition(Competition competition);

    @Query("SELECT c.filePath FROM CompetitionAttachedFile c WHERE c.filePath IS NOT NULL")
    List<String> findAllFilePath();
}
