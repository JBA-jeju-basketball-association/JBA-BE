package github.com.jbabe.repository.competitionuser;

import java.util.List;

public interface ParticipationCompetitionRepositoryCustom {
    List<ParticipationCompetition> findParticipationCompetitionsByUserId(Integer userId, boolean isAll);
}
