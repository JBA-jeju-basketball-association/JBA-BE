package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionuser.ParticipationCompetition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionFile;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionRepository;
import github.com.jbabe.repository.competitionuser.ParticipationFileRepository;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.CompetitionMapper;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.competition.participate.ModifyParticipateRequest;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CompetitionParticipationService {
    private final ParticipationCompetitionRepository participationCompetitionRepository;
    private final ParticipationFileRepository participationFileRepository;
    private final DivisionJpa divisionJpa;


    private <T> Competition getCompetitionEntryDate(T requestId) {
        if (requestId instanceof Long) {
            return divisionJpa.getCompetitionEntryDate((Long) requestId)
                    .orElseThrow(() -> new NotFoundException("신청 기록의 대회를 찾을 수 없습니다.", requestId));
        } else if (requestId instanceof Integer) {
            return divisionJpa.getCompetitionEntryDate((Integer) requestId)
                    .orElseThrow(() -> new NotFoundException("종별을 찾을 수 없습니다.", requestId));
        } else {
            throw new IllegalArgumentException("지원하지 않는 ID 타입: " + requestId.getClass().getSimpleName());
        }
    }

    public <T> void checkTheApplicationPeriod(T requestId) {
        Competition competitionEntryDate = getCompetitionEntryDate(requestId);
        LocalDate now = LocalDate.now();
        LocalDate startDate = competitionEntryDate.getParticipationStartDate();
        LocalDate endDate = competitionEntryDate.getParticipationEndDate();

        Map<String, LocalDate> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        if(startDate==null||endDate==null) throw new BadRequestException("참가 신청 기간이 미정인 상태 입니다.", response);

        if(now.isBefore(startDate) || now.isAfter(endDate)) throw new BadRequestException("참가 신청 기간이 아닙니다.", response);

    }

    @Transactional
    public long applicationForParticipationInCompetition(Integer divisionId, ParticipateRequest participateRequest, CustomUserDetails customUserDetails) {

        Division division = (Division) createDivisionOrUserById(divisionId.longValue());
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
            return new Division(((Long) divisionIdOrUserId).intValue());
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
    public void updateParticipate(Long participationCompetitionId, CustomUserDetails customUserDetails, ModifyParticipateRequest participateRequest) {
        verifyRequestChangePermissions(participationCompetitionId, customUserDetails);
        participationCompetitionRepository.updateParticipate(participationCompetitionId, participateRequest);
        List<String> deleteUrls = updateParticipateFilesAndGetDeleteUrls(participationCompetitionId, participateRequest.getFiles());
    }

    private List<String> updateParticipateFilesAndGetDeleteUrls(Long participationCompetitionId, List<FileDto> requestFiles){
        List<String> oldFileUrls = participationFileRepository.findUrlsByParticipationIdCustom(participationCompetitionId);
        System.out.println(requestFiles);
        List<String> newFileUrls = requestFiles.stream().map(file-> file.getFileUrl()).toList();

        List<String> deleteUrls = oldFileUrls.stream().filter(url -> !newFileUrls.contains(url)).toList();

        participationFileRepository.deleteByUrlListCustom(deleteUrls);

        requestFiles.stream()
                .filter(file-> !oldFileUrls.contains(file.getFileUrl()))
                .forEach(file -> makeParticipationFileEntity(file, participationCompetitionId));
        return deleteUrls;
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
