package github.com.jbabe.repository.competitionuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationFileRepository extends JpaRepository<ParticipationCompetitionFile, Long>, ParticipationFileRepositoryCustom {

}
