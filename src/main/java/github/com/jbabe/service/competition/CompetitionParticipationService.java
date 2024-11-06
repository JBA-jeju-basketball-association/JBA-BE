package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competitionuser.ParticipationCompetition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionRepository;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.CompetitionMapper;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.ParticipateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionParticipationService {
    private final ParticipationCompetitionRepository participationCompetitionRepository;

    @Transactional
    public long applicationForParticipationInCompetition(Long divisionId, ParticipateRequest participateRequest, CustomUserDetails customUserDetails) {
        Division division = (Division) createDivisionOrUserById(divisionId);
        User user = (User) createDivisionOrUserById(customUserDetails.getUserId());

        ParticipationCompetition entity = CompetitionMapper.INSTANCE
                    .participateRequestToParticipationCompetition(participateRequest, division, user);
        try {
            return participationCompetitionRepository.save(entity).getParticipationCompetitionId();
        }catch (DataIntegrityViolationException e) {
            throw new NotFoundException("divisionId가 잘못되었습니다.",  divisionId);
        }
    }

    private <T> Object createDivisionOrUserById(T divisionIdOrUserId) {
        if(divisionIdOrUserId instanceof Long) {
            return new Division((Long) divisionIdOrUserId);
        } else if(divisionIdOrUserId instanceof Integer) {
            return new User((Integer) divisionIdOrUserId );
        }
        return null;
    }

    public List<ParticipateResponse> getMyParticipate(CustomUserDetails customUserDetails, boolean isAll) {

        List<ParticipationCompetition> entity = participationCompetitionRepository
                .findParticipationCompetitionsByUserId(customUserDetails.getUserId(), isAll);

        return CompetitionMapper.INSTANCE.participationCompetitionsToParticipateResponse(entity);
    }
}
