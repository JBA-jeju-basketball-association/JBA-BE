package github.com.jbabe.repository.competitionRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRecordJpa extends JpaRepository<CompetitionRecord, Integer> {

}
