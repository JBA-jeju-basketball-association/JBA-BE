package github.com.jbabe.repository.competitionImg;

import github.com.jbabe.repository.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionImgJpa extends JpaRepository<CompetitionImg, Integer> {
    List<CompetitionImg> findAllByCompetition(Competition competition);

    @Query("SELECT c.imgUrl FROM CompetitionImg c WHERE c.imgUrl IS NOT NULL")
    List<String> findAllFilePath();
}
