package github.com.jbabe.repository.competitionRecord;

import github.com.jbabe.repository.division.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionRecordJpa extends JpaRepository<CompetitionRecord, Integer> {
    List<CompetitionRecord> findAllByDivision(Division division);

    @Query("SELECT c.filePath FROM CompetitionRecord c WHERE c.filePath IS NOT NULL")
    List<String> findAllFilePath();

    Boolean existsAllByDivision(Division division);
}
