package github.com.jbabe.repository.competitionuser;

import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipationCompetitionRepository extends JpaRepository<ParticipationCompetition, Long>, ParticipationCompetitionRepositoryCustom {

    Optional<ParticipationCompetition> findByDivisionAndUser(Division division, User user);
}
