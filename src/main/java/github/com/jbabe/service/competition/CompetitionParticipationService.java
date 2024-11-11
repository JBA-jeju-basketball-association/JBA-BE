package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competitionuser.ParticipationCompetition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionFile;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionRepository;
import github.com.jbabe.repository.competitionuser.ParticipationFileRepository;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.CompetitionMapper;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.competition.participate.ParticipateDetail;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.SimplyParticipateResponse;
import github.com.jbabe.web.dto.infinitescrolling.InfiniteScrollingCollection;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchCriteria;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionParticipationService {
    private final ParticipationCompetitionRepository participationCompetitionRepository;
    private final ParticipationFileRepository participationFileRepository;

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
        if(divisionIdOrUserId instanceof Long)
            return new Division((Long) divisionIdOrUserId);
        else if (divisionIdOrUserId instanceof Integer)
            return new User((Integer) divisionIdOrUserId);
        else {
            throw new BadRequestException("잘못된 요청", divisionIdOrUserId);
        }
    }

    public ParticipateDetail getMyParticipateById(Long participationCompetitionId) {

        List<ParticipationCompetition> entity = participationCompetitionRepository
                .findParticipationCompetitionsByUserIdOrPId(participationCompetitionId, null);
        if(entity.isEmpty())
            throw new NotFoundException("참가신청번호가 잘못되었습니다.", participationCompetitionId);

        return CompetitionMapper.INSTANCE.participationCompetitionToParticipateDetail(entity.get(0));
    }

    public InfiniteScrollingCollection<SimplyParticipateResponse, SearchCriteria> getMyParticipate(CustomUserDetails customUserDetails, SearchRequest searchRequest) {
        if(searchRequest.getIdCursor()!=null) searchRequest.makeCursorHolder();
        List<ParticipationCompetition> entity = participationCompetitionRepository
                .findParticipationCompetitionsByUserIdOrPId(customUserDetails.getUserId(), searchRequest);

        List<SimplyParticipateResponse> response = CompetitionMapper.INSTANCE.participationCompetitionsToParticipateResponse(entity);
        validParticipateList(response.get(0), searchRequest);

        return InfiniteScrollingCollection.of(response, searchRequest.getSize(), searchRequest.getSearchCriteria());
    }
    private void validParticipateList(SimplyParticipateResponse firstResponse, SearchRequest request){
        if (request.getIdCursor()==null) return;
        if(!firstResponse.valueValidity(request))
            throw new BadRequestException("커서의 값, 커서 아이디가 이 전 응답과 일치하지 않습니다.", request);
    }

    @Transactional
    public void deleteParticipate(Long participationCompetitionId, CustomUserDetails customUserDetails) {
        verifyRequestChangePermissions(participationCompetitionId, customUserDetails);
        participationCompetitionRepository.deleteByIdCustom(participationCompetitionId);
    }
    @Transactional
    public void updateParticipate(Long participationCompetitionId, CustomUserDetails customUserDetails, ParticipateRequest participateRequest) {
        verifyRequestChangePermissions(participationCompetitionId, customUserDetails);
        participationCompetitionRepository.updateParticipate(participationCompetitionId, participateRequest);
        updateParticipateFiles(participationCompetitionId, participateRequest.getFiles());
    }

    private void updateParticipateFiles(Long participationCompetitionId, List<FileDto> requestFiles){
        List<String> oldFileUrls = participationFileRepository.findUrlsByParticipationIdCustom(participationCompetitionId);
        List<String> newFileUrls = requestFiles.stream().map(file-> file.getFileUrl()).toList();

        oldFileUrls.stream().filter(url -> !newFileUrls.contains(url))
                .forEach(url -> participationFileRepository.deleteByUrlCustom(url));

        requestFiles.stream()
                .filter(file-> !oldFileUrls.contains(file.getFileUrl()))
                .forEach(file -> makeParticipationFileEntity(file, participationCompetitionId));
    }
    private void makeParticipationFileEntity(FileDto newFile, Long participationCompetitionId) {
        ParticipationCompetitionFile entity = CompetitionMapper.INSTANCE.fileDtoToParticipationCompetitionFile(newFile);
        entity.setParticipationCompetition(new ParticipationCompetition(participationCompetitionId));
        participationFileRepository.save(entity);
    }
    private void verifyRequestChangePermissions(Long participationCompetitionId, CustomUserDetails customUserDetails) {
        Integer authorId = participationCompetitionRepository
                .findParticipationCompetitionTheUserIdOfById(participationCompetitionId)
                .orElseThrow(() -> new NotFoundException("참가신청번호가 잘못되었습니다.", participationCompetitionId));

        if( !authorId.equals(customUserDetails.getUserId()) )
            throw new NotFoundException("수정 권한이 없습니다.", "로그인한 유저 id : "+customUserDetails.getUserId()+" 작성자 id : "+authorId);
    }
}
