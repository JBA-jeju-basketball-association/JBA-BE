package github.com.jbabe.repository.competitionuser;

import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationCompetitionRepositoryCustom {
    <T> List<ParticipationCompetition> findParticipationCompetitionsByUserIdOrPId(T userId);

    Optional<Integer> findParticipationCompetitionTheUserIdOfById(Long participationCompetitionId);

    void deleteByIdCustom(Long participationCompetitionId);

    void updateParticipate(Long participationCompetitionId, ParticipateRequest participateRequest);
}
