package github.com.jbabe.repository.competitionRecordFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRecordFileJpa extends JpaRepository<CompetitionRecordFile, Integer> {

}
