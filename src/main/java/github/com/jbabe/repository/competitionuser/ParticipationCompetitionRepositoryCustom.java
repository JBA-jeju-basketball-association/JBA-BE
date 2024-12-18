package github.com.jbabe.repository.competitionuser;

import github.com.jbabe.web.dto.competition.participate.ModifyParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationCompetitionRepositoryCustom {
    <T> List<ParticipationCompetition> findParticipationCompetitionsByUserIdOrPId(T userId, SearchRequest searchRequest);

    Optional<Integer> findParticipationCompetitionTheUserIdOfById(Long participationCompetitionId);

    void deleteByIdCustom(Long participationCompetitionId);

    void updateParticipate(Long participationCompetitionId, ModifyParticipateRequest participateRequest);
}
